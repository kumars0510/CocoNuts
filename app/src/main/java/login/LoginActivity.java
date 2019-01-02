package login;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.nandhini.coconuts.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import farmerdetails.HomeScreen;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import models.RegisterModel;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.signup_txt)
    TextView signup_txt;
    @BindView(R.id.loginbtn)
    Button loginbtn;

    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.forget_txtview)
            TextView forget_txtview;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        ButterKnife.bind(this);

        signup_txt.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        Realm.init(this);
        try{
            realm = Realm.getDefaultInstance();

        }catch (Exception e){
            // Get a Realm instance for this thread
            RealmConfiguration config = new RealmConfiguration
                    .Builder()
                    //.deleteRealmIfMigrationNeeded()
                    .build();
            realm = Realm.getInstance(config);

        }

        ClickListenerEvents();

    }

    private void ClickListenerEvents() {

        signup_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewRecord();
            }
        });

        forget_txtview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, VerifyPhoneActivity.class);
                intent.putExtra("mobile", username.getText().toString());
                startActivity(intent);
            }
        });
    }

    public void viewRecord() {
        RealmResults<RegisterModel> results = realm.where(RegisterModel.class).findAll();

        for (RegisterModel registerModel : results) {

//            if ((username.getText().toString() != null && username.getText().toString().equalsIgnoreCase(" ")) &&
//                    (password.getText().toString() != null && password.getText().toString().equalsIgnoreCase(" "))) {

            if (username.getText().toString().length() > 0 && password.getText().toString().length() > 0) {

                if (registerModel.getPhonenumber().equalsIgnoreCase(username.getText().toString()) &&
                        registerModel.getNew_password().equalsIgnoreCase(password.getText().toString())) {
                    signup_txt.setText("");

                    Intent intent = new Intent(LoginActivity.this, HomeScreen.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Username and Password", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Username and Password Should not be empty", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onResume() {
        username.setText("");
        password.setText("");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
