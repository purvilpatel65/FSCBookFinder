package com.example.me.fscbookfinder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.me.fscbookfinder.model.Data;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyAccountActivity extends AppCompatActivity {

     private EditText name;
     private TextView email, back;
     private Button update, delete;
     private ProgressBar progress;

     private FirebaseAuth mAuth;
     private DatabaseReference mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

         name = (EditText)findViewById(R.id.name_user);
         email = (TextView) findViewById(R.id.userEmail);
         back = (TextView) findViewById(R.id.backMyAccount);
         update = (Button)findViewById(R.id.updateAccountBtn);
         delete = (Button) findViewById(R.id.deleteAccountBtn);
         progress = (ProgressBar)findViewById(R.id.progressbarMyaccount);
         progress.setVisibility(View.INVISIBLE);

         mAuth = FirebaseAuth.getInstance();
         mData = FirebaseDatabase.getInstance().getReference("UserData");

         getInfo();

         update.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 AlertDialog.Builder builder1 = new AlertDialog.Builder(MyAccountActivity.this);

                 builder1.setMessage("Are you sure you want to update the information?");
                 builder1.setCancelable(true);

                 builder1.setPositiveButton(
                         "Yes",
                         new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int id) {
                                 updateInfo();
                                 dialog.cancel();
                             }
                         });

                 builder1.setNegativeButton(
                         "No",
                         new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int id) {

                                 dialog.cancel();
                             }
                         });

                 AlertDialog alert11 = builder1.create();
                 alert11.show();
             }
         });

         delete.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 AlertDialog.Builder builder1 = new AlertDialog.Builder(MyAccountActivity.this);

                 builder1.setMessage("Are you sure you want to delete this account permanently?");
                 builder1.setCancelable(true);

                 builder1.setPositiveButton(
                         "Yes",
                         new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int id) {
                                 deleteAccount();
                                 dialog.cancel();
                             }
                         });

                 builder1.setNegativeButton(
                         "No",
                         new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int id) {

                                 dialog.cancel();
                             }
                         });

                 AlertDialog alert11 = builder1.create();
                 alert11.show();

             }
         });


    }

    private void getInfo(){
        email.setText(mAuth.getCurrentUser().getEmail());

        mData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    if(snapshot.getValue(Data.class).getUID().equals(mAuth.getCurrentUser().getUid())){
                        name.setText(snapshot.getValue(Data.class).getName());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyAccountActivity.this, SearchActivity.class));
                finish();
            }
        });

    }

   private void updateInfo(){

        if(name.getText().toString().trim().equals("")){
           name.setError("Field Required!");
        }
        else{
            progress.setVisibility(View.VISIBLE);
            mData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        if(snapshot.getValue(Data.class).getUID().equals(mAuth.getCurrentUser().getUid())){
                           snapshot.child("name").getRef().setValue(name.getText().toString().trim());
                           progress.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Account Successfully updated!", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), databaseError.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            });
        }

   }


   private void deleteAccount(){
        final String tempUid = mAuth.getCurrentUser().getUid();
        progress.setVisibility(View.VISIBLE);
      mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
              if(task.isSuccessful()){
                  mData.addListenerForSingleValueEvent(new ValueEventListener() {
                      @Override
                      public void onDataChange(DataSnapshot dataSnapshot) {
                          for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                              if(snapshot.getValue(Data.class).getUID().equals(tempUid)){
                                  snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {
                                          if(task.isSuccessful()){
                                              progress.setVisibility(View.GONE);
                                              Toast.makeText(getApplicationContext(), "Successfully deleted the account!", Toast.LENGTH_SHORT).show();
                                              startActivity(new Intent(MyAccountActivity.this, LogInActivity.class));
                                              finish();
                                          }else {
                                              progress.setVisibility(View.GONE);
                                          }

                                      }
                                  });
                                  break;
                              }
                          }
                      }

                      @Override
                      public void onCancelled(DatabaseError databaseError) {
                          Toast.makeText(getApplicationContext(), databaseError.getMessage().toString(), Toast.LENGTH_LONG);
                      }
                  });
              }

          }
      }).addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
              progress.setVisibility(View.GONE);
              Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
          }
      });
   }

}

