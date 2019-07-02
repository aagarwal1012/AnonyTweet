package com.ayush.anonytweet;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    private static final int ACTIVITY_FORGOT_PASSWORD_LAYOUT = R.layout.activity_forgot_password;

    private static final int FORGOT_EMAIL_ID = R.id.forgot_email;
    private static final int FORGOT_BTN_RESET_ID = R.id.forgot_btn_reset;
    private static final int FORGOT_BTN_BACK_ID = R.id.forgot_btn_back;
    private static final int ACTIVITY_FORGOT_PASSWORD_ID = R.id.activity_forgot_password;

    private EditText input_email;
    private RelativeLayout activity_forgot;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ACTIVITY_FORGOT_PASSWORD_LAYOUT);

        //View
        input_email = findViewById(FORGOT_EMAIL_ID);
        Button btnResetPass = findViewById(FORGOT_BTN_RESET_ID);
        TextView btnBack = findViewById(FORGOT_BTN_BACK_ID);
        activity_forgot = findViewById(ACTIVITY_FORGOT_PASSWORD_ID);

        btnResetPass.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        //Init Firebase
        auth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.forgot_btn_back) {
            startActivity(new Intent(this, login.class));
            finish();
        } else if (id == R.id.forgot_btn_reset) {
            resetPassword(input_email.getText().toString());
        }
    }

    private void resetPassword(final String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Snackbar snackBar = Snackbar.make(activity_forgot, "We have sent password to email: " + email, Snackbar.LENGTH_SHORT);
                        snackBar.show();
                    } else {
                        Snackbar snackBar = Snackbar.make(activity_forgot, "Failed to send password", Snackbar.LENGTH_SHORT);
                        snackBar.show();
                    }
                });
    }

}
