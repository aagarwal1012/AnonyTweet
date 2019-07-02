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

    private EditText input_new_password;
    private RelativeLayout activity_changePass;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        input_new_password = findViewById(R.id.new_password);
        Button btnChangePass = findViewById(R.id.btn_change_pass);
        activity_changePass = findViewById(R.id.activity_change_pass);

        btnChangePass.setOnClickListener(this);

        //Init Firebase
        auth = FirebaseAuth.getInstance();


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_change_pass)
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
