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
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private Button btnSignup;
    private TextView btnLogin;
    private TextView btnForgotPass;
    private EditText input_email;
    private EditText input_pass;
    private EditText input_name;
    private RelativeLayout activity_sign_up;
    private Snackbar snackbar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //View
        btnSignup = findViewById(R.id.signup_btn_register);
        btnLogin = findViewById(R.id.signup_btn_login);
        btnForgotPass = findViewById(R.id.signup_btn_forgot_pass);
        input_email = findViewById(R.id.signup_email);
        input_pass = findViewById(R.id.signup_password);
        input_name = findViewById(R.id.signup_name);
        activity_sign_up = findViewById(R.id.activity_sign_up);

        btnSignup.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnForgotPass.setOnClickListener(this);

        //Init Firebase
        auth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signup_btn_login) {
            startActivity(new Intent(SignUp.this, login.class));
            finish();
        } else if (view.getId() == R.id.signup_btn_forgot_pass) {
            startActivity(new Intent(SignUp.this, ForgotPassword.class));
            finish();
        } else if (view.getId() == R.id.signup_btn_register) {
            signUpUser(input_email.getText().toString(), input_pass.getText().toString());
        }
    }

    private void signUpUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        snackbar = Snackbar.make(activity_sign_up, "Error: " + task.getException(), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else {

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(input_name.getText().toString())
                                .build();
                        auth.getCurrentUser().updateProfile(profileUpdates);

                        snackbar = Snackbar.make(activity_sign_up, "Register success! ", Snackbar.LENGTH_SHORT);
                        snackbar.show();

                        startActivity(new Intent(SignUp.this, DashBoard.class));
                    }
                });
    }

}
