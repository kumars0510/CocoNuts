package farmerdetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nandhini.coconuts.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import commonFiles.Constants;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import models.FarmerDetailsModel;

public class FarmerListActivity extends AppCompatActivity {
    Realm realm;
    RealmResults<FarmerDetailsModel> results;
    ArrayList<String> farmerNameList = new ArrayList<String>();

    @BindView(R.id.farmername_listview)
    ListView farmername_listview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farmerlist);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setTitle("Vendors");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        realm = Realm.getDefaultInstance();
        results = realm.where(FarmerDetailsModel.class).findAll();

        getFarmerDetails();

        farmername_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (FarmerDetailsModel farmerDetailsModel : results) {
                    Constants.farmerDetailsModel = null;
                    if (farmerDetailsModel.getFarmerName().equalsIgnoreCase(results.get(position).getFarmerName())) {
                        Constants.farmerDetailsModel = farmerDetailsModel;
                        Intent intent = new Intent(FarmerListActivity.this, FarmerContactActivity.class);
                        startActivity(intent);
                        break;
                    }
                }
            }
        });
    }

    private void getFarmerDetails() {
        RealmResults<FarmerDetailsModel> list = realm.where(FarmerDetailsModel.class)
                .findAllSorted("harvestDate", Sort.ASCENDING);

        for (FarmerDetailsModel farmerDetailsModel : list) {
            farmerNameList.add(farmerDetailsModel.getFarmerName());
        }

        FarmerListAdapter farmerListAdapter = new FarmerListAdapter(this, farmerNameList);
        farmername_listview.setAdapter(farmerListAdapter);
    }

    private class FarmerListAdapter extends BaseAdapter {

        private Activity activity;
        private ArrayList farmerName;
        private LayoutInflater inflater = null;

        public FarmerListAdapter(Activity activity, ArrayList farmerName) {
            this.activity = activity;
            this.farmerName = farmerName;
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

            harvestDate.setVisibility(View.GONE);
            // Setting all values in list view
            farmername.setText(farmerName.get(position).toString());
            return view;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
