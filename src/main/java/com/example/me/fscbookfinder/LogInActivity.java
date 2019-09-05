package com.example.me.fscbookfinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {


    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordButton;
    private Button signUpButton;
    private ProgressBar progressBar;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.loginBtn);
        forgotPasswordButton = (TextView) findViewById(R.id.forgotPasswordBtn);
        signUpButton = (Button) findViewById(R.id.registerBtn);
        progressBar = (ProgressBar)findViewById(R.id.progressbarLogIn);
        progressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
       if(currentUser!=null && currentUser.isEmailVerified())
       {
           Intent homeIntent = new Intent(LogInActivity.this, SearchActivity.class);
           startActivity(homeIntent);
       }
    }


    public void login(View view) {

       signIn(usernameEditText.getText().toString().trim(), passwordEditText.getText().toString().trim());

    }

    public void register(View view) {
        //take to registration form

        Intent registerIntent = new Intent(LogInActivity.this, RegisterActivity.class);
        startActivity(registerIntent);

    }

    public void forgot_password(View view) {
       resetPassword();
    }

    private void signIn(String email, String password) {

        if (!validateForm()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // [START sign_in_with_email]+++++++++++++++++++++++++++
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                          if(mAuth.getCurrentUser().isEmailVerified()) {
                              // Sign in success, update UI with the signed-in user's information
                              Intent homeIntent = new Intent(LogInActivity.this, SearchActivity.class);
                              startActivity(homeIntent);
                          }else{
                              Toast.makeText(LogInActivity.this, "Please verify your email address!", Toast.LENGTH_LONG).show();
                          }

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        progressBar.setVisibility(View.INVISIBLE);
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = usernameEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            usernameEditText.setError("Required.");
            valid = false;
        } else {
            usernameEditText.setError(null);
        }

        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Required.");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }

    private void resetPassword(){
        if(usernameEditText.getText().toString().trim().equals("")){
            usernameEditText.setError("Please enter your email to reset password.");
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            mAuth.sendPasswordResetEmail(usernameEditText.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(LogInActivity.this, "A link to reset password has been sent to your email. Follow the link to reset password. Thank You!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LogInActivity.this, "Reset password failed. Please try again!", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

}
