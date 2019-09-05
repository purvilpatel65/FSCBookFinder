package com.example.me.fscbookfinder;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.me.fscbookfinder.model.Data;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    private TextView registerTextView, back;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        firstNameEditText = (EditText) findViewById(R.id.first_name);
        lastNameEditText = (EditText) findViewById(R.id.last_name);
        emailEditText = (EditText) findViewById(R.id.email);
        passwordEditText = (EditText) findViewById(R.id.password);
        registerButton = (Button) findViewById(R.id.registerform_btn);
        progressBar = (ProgressBar) findViewById(R.id.progressbarRegister);
        back = (TextView)findViewById(R.id.backBtn);

        progressBar.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference("UserData");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LogInActivity.class));
                finish();
            }
        });
    }

    public void end_registration(View view) {

        final String firstName = firstNameEditText.getText().toString();
        final String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString().toLowerCase();
        String password = passwordEditText.getText().toString();


        //entry validation; check if fields are empty
        if (firstName.isEmpty() || lastName.isEmpty() ||  email.isEmpty() || password.isEmpty()) {
            firstNameEditText.setError("Empty fields! Fill all fields and try again.");
            lastNameEditText.setError("Empty fields! Fill all fields and try again.");
            emailEditText.setError("Empty fields! Fill all fields and try again.");
            passwordEditText.setError("Empty fields! Fill all fields and try again.");

        }
        //validate that first name is more than three characters and less than thirty characters
        else if (firstName.length() < 3 || firstName.length() > 30 )
        {
            firstNameEditText.setError("The First Name field has invalid input. Try again.");
        }
        //validate that last name is more than two characters and less than thirty characters
        else if (lastName.length() < 2 || lastName.length() > 30)
        {
            firstNameEditText.setError("The Family (Last) Name field has invalid input. Try again.");
        }
        //validate that email entry follows requested format, has ending of @farmingdale.edu
        else if (email.contains("@farmingdale.edu") == false)
        {
            emailEditText.setError("The email entered is not an FSC email account. Try again.");
        }

        //validate that password is more than eight characters
        else if (password.length() < 8)
        {
            passwordEditText.setError("The Password field has invalid input. Try again.");
        }
        //successful registration, add to database and return to login screen
        else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);

                                String tempName = firstName + " " + lastName;
                                String tempUID = mAuth.getCurrentUser().getUid();
                                String tempEmail = mAuth.getCurrentUser().getEmail();

                                Data data = new Data(tempUID, tempEmail, tempName, 3);
                                final String uploadId = mRef.push().getKey();
                                mRef.child(uploadId).setValue(data);
                                // Sign in success, update UI with the signed-in user's information

                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            firstNameEditText.setText("");
                                            lastNameEditText.setText("");
                                            emailEditText.setText("");
                                            passwordEditText.setText("");
                                            Toast.makeText(getApplicationContext(), "Registration successful! Please check your email for verification link.", Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(getApplicationContext(), "Please click 'register' button again for verification link. Thank you!.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });


                            } else {
                                // If sign in fails, display a message to the user.
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_LONG).show();

                                try
                                {
                                    throw task.getException();
                                }
                                // if user enters wrong email.
                                catch (FirebaseAuthWeakPasswordException weakPassword)
                                {
                                    passwordEditText.setError("Weak Password. Try again.");

                                    // TODO: take your actions!
                                }
                                // if user enters wrong password.
                                catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                                {
                                    emailEditText.setError("Invalid Email. Try again.");

                                    // TODO: Take your action
                                }
                                catch (FirebaseAuthUserCollisionException existEmail)
                                {
                                    emailEditText.setError("Email already exist. Try again with different email!");

                                    // TODO: Take your action
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(getApplicationContext(), e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }

                            // ...
                        }
                    });


        }
    }


}
