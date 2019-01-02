package login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.nandhini.coconuts.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import models.RegisterModel;

public class SignUpActivity extends AppCompatActivity {

    Realm realm;

    @BindView(R.id.phone_number)
    EditText phone_number;
    @BindView(R.id.new_password)
    EditText new_password;
    @BindView(R.id.confirm_password)
    EditText confirm_password;
    @BindView(R.id.submitbtn)
    Button submitbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_screen);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        ClickEvents();


    }

    private void ClickEvents() {
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone_number.getText().toString() != null && new_password.getText().toString() != null && confirm_password.getText().toString() != null) {
                    addUserRecord();
                } else {
                    Toast.makeText(SignUpActivity.this, "Fill all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addUserRecord() {
        realm.beginTransaction();
        RegisterModel registerModel = realm.createObject(RegisterModel.class);
        registerModel.setPhonenumber(phone_number.getText().toString());
        registerModel.setNew_password(new_password.getText().toString());
        registerModel.setConfirm_password(confirm_password.getText().toString());
        realm.commitTransaction();

        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);

    }
}
