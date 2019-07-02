package com.ayush.anonytweet;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class changePassword extends AppCompatActivity implements View.OnClickListener {

    private static final int ACTIVITY_CHANGE_PASSWORD_LAYOUT = R.layout.activity_change_password;

    private static final int NEW_PASSWORD_ID = R.id.new_password;
    private static final int BTN_CHANGE_PASS_ID = R.id.btn_change_pass;
    private static final int ACTIVITY_CHANGE_PASS_ID = R.id.activity_change_pass;

    private EditText input_new_password;
    private RelativeLayout activity_changePass;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ACTIVITY_CHANGE_PASSWORD_LAYOUT);

        input_new_password = findViewById(NEW_PASSWORD_ID);
        Button btnChangePass = findViewById(BTN_CHANGE_PASS_ID);
        activity_changePass = findViewById(ACTIVITY_CHANGE_PASS_ID);

        btnChangePass.setOnClickListener(this);

        //Init Firebase
        auth = FirebaseAuth.getInstance();


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == BTN_CHANGE_PASS_ID)
            changePassword(input_new_password.getText().toString());
    }

    private void changePassword(String newPassword) {
        FirebaseUser user = auth.getCurrentUser();
        user.updatePassword(newPassword).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Snackbar snackBar = Snackbar.make(activity_changePass, "Password changed", Snackbar.LENGTH_SHORT);
                snackBar.show();
            }
        });
    }
}
