package farmerdetails;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nandhini.coconuts.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import commonFiles.Constants;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import models.FarmerDetailsModel;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;
import android.widget.Toolbar;

public class HomeScreen extends AppCompatActivity {

    @BindView(R.id.symbol_searchView)
    AutoCompleteTextView symbol_searchView;
    @BindView(R.id.closeTxt)
    TextView closeTxt;
    @BindView(R.id.search_listview)
    ListView search_listview;
    @BindView(R.id.nav_icon_list)
    LinearLayout nav_icon_list;

    @BindView(R.id.nav_add_icon)
    Button nav_add_icon;
    @BindView(R.id.nav_contact_icon)
    Button nav_contact_icon;
    @BindView(R.id.nav_history_icon)
    Button nav_history_icon;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    Realm realm;
    RealmResults<FarmerDetailsModel> results;
    ArrayList<String> farmerNameList = new ArrayList<String>();
    ArrayList<String> harvestDateList = new ArrayList<String>();

    ArrayList<String> filter_farmerNameList = new ArrayList<String>();
    ArrayList<String> filter_harvestDateList = new ArrayList<String>();

    boolean checkFIlterfalg = false;
    String filterName;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Vendor Details");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_drawer);

//        ActionBar actionBar = getSupportActionBar();
//        //actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
//        actionBar.setTitle("Vendor Details");
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setIcon(R.drawable.ic_drawer);

        Realm.init(this);
        realm = Realm.getDefaultInstance();
        results = realm.where(FarmerDetailsModel.class).findAll();
        getHarvestDetailsFromDB();

        nav_add_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, FarmerDetailsActivity.class);
                startActivity(intent);
            }
        });

        nav_contact_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, FarmerListActivity.class);
                startActivity(intent);
            }
        });

        if (farmerNameList != null && farmerNameList.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (this, android.R.layout.select_dialog_item, farmerNameList);
            symbol_searchView.setAdapter(adapter);

            symbol_searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object selectedItem = parent.getItemAtPosition(position);

                    RealmResults<FarmerDetailsModel> list = realm.where(FarmerDetailsModel.class)
                            .findAllSorted("harvestDate", Sort.ASCENDING);

                    for (FarmerDetailsModel farmerDetailsModel : list) {
                        if (selectedItem.equals(farmerDetailsModel.getFarmerName())) {
                            checkFIlterfalg = true;

                            filter_farmerNameList.clear();
                            filter_harvestDateList.clear();

                            filterName = farmerDetailsModel.getFarmerName();
                            filter_farmerNameList.add(farmerDetailsModel.getFarmerName());
                            filter_harvestDateList.add(farmerDetailsModel.getHarvestDate());
                        }
                    }

                    FarmerDetailsAdapter farmerDetailsAdapter = new FarmerDetailsAdapter(HomeScreen.this, filter_farmerNameList, filter_harvestDateList);
                    search_listview.setAdapter(farmerDetailsAdapter);
                }
            });
        }

        symbol_searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if (charSequence.length() == 0) {
                    if (farmerNameList != null && farmerNameList.size() > 0) {
                        FarmerDetailsAdapter farmerDetailsAdapter = new FarmerDetailsAdapter(HomeScreen.this, farmerNameList, harvestDateList);
                        search_listview.setAdapter(farmerDetailsAdapter);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        closeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                symbol_searchView.setText("");
                if (farmerNameList != null && farmerNameList.size() > 0) {
                    FarmerDetailsAdapter farmerDetailsAdapter = new FarmerDetailsAdapter(HomeScreen.this, farmerNameList, harvestDateList);
                    search_listview.setAdapter(farmerDetailsAdapter);
                }
            }
        });

        search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Constants.farmerDetailsModel = null;

                for (FarmerDetailsModel farmerDetailsModel : results) {
                    if (filterName != null && filterName.length() > 0) {
                        if (farmerDetailsModel.getFarmerName().equalsIgnoreCase(filterName)) {
                            navigateToFarmerContact(farmerDetailsModel);
                        }
                    } else {
                        if (farmerDetailsModel.getFarmerName().equalsIgnoreCase(results.get(position).getFarmerName())) {
                            navigateToFarmerContact(farmerDetailsModel);
                        }
                    }

                }
            }
        });

    }

    private void navigateToFarmerContact(FarmerDetailsModel farmerDetailsModel) {
        Constants.farmerDetailsModel = farmerDetailsModel;
        filterName = "";
        Intent intent = new Intent(HomeScreen.this, FarmerContactActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("NNNN Resume-----");
        //getHarvestDetailsFromDB();

    }

    private void getHarvestDetailsFromDB() {
//        RealmResults<FarmerDetailsModel> results = realm.where(FarmerDetailsModel.class).findAll();
//        for (FarmerDetailsModel farmerDetailsModel : results) {
//            farmerNameList.add(farmerDetailsModel.getFarmerName());
//            harvestDateList.add(farmerDetailsModel.getHarvestDate());
//        }
        RealmResults<FarmerDetailsModel> list = realm.where(FarmerDetailsModel.class)
                .findAllSorted("harvestDate", Sort.ASCENDING);

        for (FarmerDetailsModel farmerDetailsModel : list) {
            farmerNameList.add(farmerDetailsModel.getFarmerName());
            harvestDateList.add(farmerDetailsModel.getHarvestDate());
        }

        FarmerDetailsAdapter farmerDetailsAdapter = new FarmerDetailsAdapter(this, farmerNameList, harvestDateList);
        search_listview.setAdapter(farmerDetailsAdapter);
    }


    private class FarmerDetailsAdapter extends BaseAdapter {

        private Activity activity;
        private ArrayList farmerName;
        private ArrayList harvestdate;
        private LayoutInflater inflater = null;

        public FarmerDetailsAdapter(Activity activity, ArrayList farmerName, ArrayList harvestdate) {
            this.activity = activity;
            this.farmerName = farmerName;
            this.harvestdate = harvestdate;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return farmerName.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null)
                view = inflater.inflate(R.layout.list_row, null);

            TextView farmername = (TextView) view.findViewById(R.id.farmerName); // title
            TextView harvestDate = (TextView) view.findViewById(R.id.harvestDate); // artist name


            // Setting all values in list view

            farmername.setText(farmerName.get(position).toString());
            harvestDate.setText(harvestdate.get(position).toString());
            return view;
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}