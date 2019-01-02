package farmerdetails;

import android.app.DatePickerDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nandhini.coconuts.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import commonFiles.Constants;
import io.realm.Realm;
import models.FarmerDetailsModel;

public class FarmerDetailsActivity extends AppCompatActivity {

    @BindView(R.id.farmer_name)
    EditText farmer_name;
    @BindView(R.id.farmer_phonenumber)
    EditText farmer_phonenumber;
    @BindView(R.id.harvest_date)
    EditText harvest_date;
    @BindView(R.id.farmer_address)
    EditText farmer_address;
    @BindView(R.id.farmer_unit)
    EditText farmer_unit;
    @BindView(R.id.farmer_price)
    EditText farmer_price;

    @BindView(R.id.addcontact_checkbox)
    CheckBox addcontact_checkbox;
    @BindView(R.id.submit_btn)
    Button submit_btn;

    Boolean checkCheckBoxFlag = false;
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farmer_details);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setTitle("Farmer Details");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // DatePicker for Harvest date
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //updateLabel(); // Update the date in Harvest date field
                getDateTime();
            }
        };

        ClickEvent(); // Event for all the click listener
        //getDateTime(); // Get Current date and time

    }

    // To get a 45th day date from current date
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        getFutureDate(date, 45);
        return dateFormat.format(date);
    }

    private void ClickEvent() {
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addcontact_checkbox.isChecked()) {
                    checkCheckBoxFlag = true;
                } else {
                    checkCheckBoxFlag = false;
                }
                /*Farmer name, Phone Number Needed to add Contact in Mobile
                Checkbox - Should be check*/
                if (farmer_name.getText().toString() != null && farmer_phonenumber.getText().toString() != null && harvest_date.getText().toString() != null && checkCheckBoxFlag) {
                    addContact(farmer_name.getText().toString(), farmer_phonenumber.getText().toString());
                    // Add remainder in Calendar
                    showReminderEvent();
                    addFarmerRecord(); // Adding Farmer details to Database
                    Intent intent = new Intent(FarmerDetailsActivity.this, HomeScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(FarmerDetailsActivity.this, "Fill all the details", Toast.LENGTH_SHORT).show();
                }

            }
        });

        harvest_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(FarmerDetailsActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if (Constants.futureDate != null && Constants.futureDate.length() > 0) {
            harvest_date.setText(Constants.futureDate);
        }
    }

    /*Automatically add contact to phone contact*/
    private void addContact(String name, String phone) {
        ArrayList<ContentProviderOperation> ops =
                new ArrayList<ContentProviderOperation>();
        int rawContactID = ops.size();

        // Adding insert operation to operations list
        // to insert a new raw contact in the table ContactsContract.RawContacts
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // to insert display name in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());

        // to insert Mobile Number in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        try {
            // Executing all the insert operations as a single database transaction
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Toast.makeText(getBaseContext(), "Contact is successfully added", Toast.LENGTH_SHORT).show();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    //Adding Remainder in calendar after 45th day from current date
    public void showReminderEvent() {
        String eventTitle = "Harvest Date"; //This is event title
        String eventDescription = "Call to harvester:)"; //This is event description
        String eventDate = Constants.futureDate; //This is the event date
        System.out.println("NNNNN eventDate  ------ " + eventDate);
        String eventLocation = "Chennai"; //This is the address for your event location

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dEventDate = dateFormat.parse(eventDate); //Date is formatted to standard format “MM/dd/yyyy”
            cal.setTime(dEventDate);
            cal.add(Calendar.DATE, 0); //It will return one day before calendar of eventDate
        } catch (Exception e) {
            e.printStackTrace();
        }

        String reminderDate = dateFormat.format(cal.getTime());
        String reminderDayStart = reminderDate + " 00:00:00";
        String reminderDayEnd = reminderDate + " 23:59:59";
        long startTimeInMilliseconds = 0, endTimeInMilliseconds = 0;

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            Date SDate = formatter.parse(reminderDayStart);
            Date EDate = formatter.parse(reminderDayEnd);
            startTimeInMilliseconds = SDate.getTime();
            endTimeInMilliseconds = EDate.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.DTSTART, startTimeInMilliseconds);
        values.put(CalendarContract.Events.DTEND, endTimeInMilliseconds);
        values.put(CalendarContract.Events.TITLE, eventTitle);
        values.put(CalendarContract.Events.DESCRIPTION, eventDescription);
        values.put(CalendarContract.Events.EVENT_LOCATION, eventLocation);

        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.RRULE, "FREQ=HOURLY;COUNT=1");
        values.put(CalendarContract.Events.HAS_ALARM, 1);
        Uri eventUri;

        if (Build.VERSION.SDK_INT >= 8) {
            eventUri = Uri.parse("content://com.android.calendar/events");
        } else {
            eventUri = Uri.parse("content://calendar/events");
        }
        // insert event to calendar
        Uri uri = cr.insert(eventUri, values);
        //add reminder for event
        try {
            Uri REMINDERS_URI;
            long id = -1;
            id = Long.parseLong(uri.getLastPathSegment());
            ContentValues reminders = new ContentValues();
            reminders.put(CalendarContract.Reminders.EVENT_ID, id);
            reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            reminders.put(CalendarContract.Reminders.MINUTES, 1);
            if (Build.VERSION.SDK_INT >= 8) {
                REMINDERS_URI = Uri.parse("content://com.android.calendar/reminders");
            } else {
                REMINDERS_URI = Uri.parse("content://calendar/reminders");
            }
            Uri remindersUri = getContentResolver().insert(REMINDERS_URI, reminders);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Finding future Date from current date
    public void getFutureDate(Date currentDate, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, days);
        Date futureDate = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Constants.futureDate = String.valueOf(dateFormat.format(futureDate));
        harvest_date.setText(Constants.futureDate);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // moveTaskToBack(true);
        super.onBackPressed();
        this.finish();
    }

    public void addFarmerRecord() {
        realm.beginTransaction();
        FarmerDetailsModel farmerDetailsModel = realm.createObject(FarmerDetailsModel.class);
        farmerDetailsModel.setFarmerName(farmer_name.getText().toString());
        farmerDetailsModel.setFarmerPhonenumber(farmer_phonenumber.getText().toString());
        farmerDetailsModel.setFarmerAddress(farmer_address.getText().toString());
        farmerDetailsModel.setHarvestDate(harvest_date.getText().toString());
        farmerDetailsModel.setContactCheckBox(checkCheckBoxFlag);
        farmerDetailsModel.setFarmerUnit(farmer_unit.getText().toString());
        farmerDetailsModel.setFarmerPrice(farmer_price.getText().toString());
        realm.commitTransaction();
    }
}
