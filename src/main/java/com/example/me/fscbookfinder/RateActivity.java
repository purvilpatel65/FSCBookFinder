package com.example.me.fscbookfinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.me.fscbookfinder.model.Data;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RateActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private Button go, submit;
    private TextView name, email;
    private RatingBar rate;
    private EditText searchUser;
    private String tempUser="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        toolbarSetUp();

        searchUser = (EditText)findViewById(R.id.user_search_box) ;
        go = (Button)findViewById(R.id.goBtn);
        submit = (Button)findViewById(R.id.submitButton);
        name = (TextView)findViewById(R.id.name_search_txt);
        email = (TextView)findViewById(R.id.email_search_Txt);
        rate = (RatingBar)findViewById(R.id.rateBar);

        name.setVisibility(View.INVISIBLE);
        email.setVisibility(View.INVISIBLE);
        rate.setVisibility(View.INVISIBLE);
        submit.setVisibility(View.INVISIBLE);


    }

    private void toolbarSetUp() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_home));
    }

    public void findUser(View view){

        tempUser = searchUser.getText().toString().trim();

        if(tempUser.equals(""))
            searchUser.setError("User's email required!");
        else if(!tempUser.toLowerCase().contains("@farmingdale.edu"))
            searchUser.setError("User's email must contains \"farmingdale.edu\"!");
        else{
            FirebaseDatabase.getInstance().getReference("UserData").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean check = false;
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        if(snapshot.getValue(Data.class).getuName().equalsIgnoreCase(tempUser)){

                            name.setText(snapshot.getValue(Data.class).getName());
                            email.setText(snapshot.getValue(Data.class).getuName());

                            name.setVisibility(View.VISIBLE);
                            email.setVisibility(View.VISIBLE);
                            rate.setVisibility(View.VISIBLE);
                            submit.setVisibility(View.VISIBLE);

                            check=true;

                            break;
                        }

                        }

                        if(check==false) {
                            name.setText("No Result Found!");
                            email.setVisibility(View.INVISIBLE);
                            rate.setVisibility(View.INVISIBLE);
                            submit.setVisibility(View.INVISIBLE);
                        }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                   Toast.makeText(getApplicationContext(), databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onSubmit(View view) {


        if (submit.getText().toString().equalsIgnoreCase("SUBMIT")) {

            if (rate.getRating() == 0) {
                Toast.makeText(RateActivity.this, "You must assign rating to user!", Toast.LENGTH_LONG).show();
            } else {

                FirebaseDatabase.getInstance().getReference("UserData").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.getValue(Data.class).getuName().equalsIgnoreCase(tempUser)) {

                                float getRate = rate.getRating();
                                float updateRate = ((float) snapshot.getValue(Data.class).getRating() + getRate) / 2;

                                snapshot.child("rating").getRef().setValue(updateRate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        rate.setRating(0);
                                        submit.setText("Back");
                                        Toast.makeText(RateActivity.this, "Thanks for your feedback!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RateActivity.this, "Rating fails! Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }else{
            startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            finish();
        }
    }

}
