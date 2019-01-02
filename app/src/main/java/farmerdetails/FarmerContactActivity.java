package farmerdetails;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nandhini.coconuts.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import commonFiles.Constants;

public class FarmerContactActivity extends AppCompatActivity {

    @BindView(R.id.farmerName)
    TextView farmerName;
    @BindView(R.id.farmerPhonenumber)
    TextView farmerPhonenumber;
    @BindView(R.id.harvestDate)
    TextView harvestDate;
    @BindView(R.id.farmerAddress)
    TextView farmerAddress;
    @BindView(R.id.unitTxt)
    TextView unitTxt;
    @BindView(R.id.priceTxt)
    TextView priceTxt;
    @BindView(R.id.done_btn)
    Button done_btn;
    @BindView(R.id.call_btn)
    Button call_btn;

    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farmercontact_screen);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Vendors List");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (Constants.farmerDetailsModel != null) {
            farmerName.setText(Constants.farmerDetailsModel.getFarmerName());
            farmerPhonenumber.setText(Constants.farmerDetailsModel.getFarmerPhonenumber());
            harvestDate.setText(Constants.farmerDetailsModel.getHarvestDate());
            farmerAddress.setText(Constants.farmerDetailsModel.getFarmerAddress());
            unitTxt.setText(Constants.farmerDetailsModel.getFarmerUnit());
            priceTxt.setText(Constants.farmerDetailsModel.getFarmerPrice());
        }

        call_btn.setText("Call to " + farmerName.getText().toString());

        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(farmerPhonenumber.getText().toString())) {
                    if (checkPermission(Manifest.permission.CALL_PHONE)) {
                        String dial = "tel:" + farmerPhonenumber.getText().toString();
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                    } else {
                        Toast.makeText(FarmerContactActivity.this, "Permission Call Phone denied", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FarmerContactActivity.this, "Enter a phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (checkPermission(Manifest.permission.CALL_PHONE)) {
            call_btn.setEnabled(true);
        } else {
            call_btn.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MAKE_CALL_PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MAKE_CALL_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    call_btn.setEnabled(true);
                    Toast.makeText(this, "You can call the number by clicking on the button", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
