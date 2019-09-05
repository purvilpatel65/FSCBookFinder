package com.example.me.fscbookfinder;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.me.fscbookfinder.model.Data;
import com.example.me.fscbookfinder.model.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Description_Activity extends AppCompatActivity {

    private String tempTitle, tempUid;
    private double tempPrice;
    private TextView Gallery;
    private Dialog dialog;
    private Button email, goBack;
    private RatingBar rate;

    private TextView author, publisher, edition, isbn, title, author2, price, sellerName, sellerEmail;
    private String coverImage="", img1="", img2="", img3="", img4="";
    private ImageView cImg;

   private FirebaseAuth mAuth;
   private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_);
        dialog = new Dialog(this);
        Gallery = (TextView)findViewById(R.id.gallery);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        author = (TextView)findViewById(R.id.authorTxt);
        publisher = (TextView)findViewById(R.id.publisherTxt);
        edition = (TextView)findViewById(R.id.editionTxt);
        isbn = (TextView)findViewById(R.id.isbnTxt);
        author2 = (TextView)findViewById(R.id.authorTxt2);
        price = (TextView)findViewById(R.id.priceTxt);
        sellerName = (TextView)findViewById(R.id.sellerNameTxt);
        title = (TextView)findViewById(R.id.titleTxt);
        sellerEmail = (TextView)findViewById(R.id.emailTxt);
        cImg = (ImageView)findViewById(R.id.coverImage);
        email = (Button)findViewById(R.id.emailBtn);
        goBack = (Button)findViewById(R.id.goBackBtn);
        rate = (RatingBar)findViewById(R.id.mRating);

        Intent getIntent = getIntent();
        tempTitle = getIntent.getStringExtra("Title");
        tempUid = getIntent.getStringExtra("UID");
        tempPrice = getIntent.getDoubleExtra("Price", 0);

        Gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageDialogPopUp();
            }
        });

        setDescription();
        setRating();


    }

    private void imageDialogPopUp()
    {
        dialog.setContentView(R.layout.image_dialog);

        TextView cancel = (TextView)dialog.findViewById(R.id.cancelBtn);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        LinearLayout gallery = (LinearLayout)dialog.findViewById(R.id.gallery);
        LayoutInflater inflater = LayoutInflater.from(this);



            if(!coverImage.equals(""))
            {
                View view = inflater.inflate(R.layout.image_item, gallery, false);
                ImageView itemImages = (ImageView)view.findViewById(R.id.images);
                Picasso.with(getApplicationContext()).load(coverImage).into(itemImages);
                gallery.addView(view);
            }

        if(!img1.equals(""))
        {
            View view = inflater.inflate(R.layout.image_item, gallery, false);
            ImageView itemImages = (ImageView)view.findViewById(R.id.images);
            Picasso.with(getApplicationContext()).load(img1).into(itemImages);
            gallery.addView(view);
        }

        if(!img2.equals(""))
        {
            View view = inflater.inflate(R.layout.image_item, gallery, false);
            ImageView itemImages = (ImageView)view.findViewById(R.id.images);
            Picasso.with(getApplicationContext()).load(img2).into(itemImages);
            gallery.addView(view);
        }

        if(!img3.equals(""))
        {
            View view = inflater.inflate(R.layout.image_item, gallery, false);
            ImageView itemImages = (ImageView)view.findViewById(R.id.images);
            Picasso.with(getApplicationContext()).load(img3).into(itemImages);
            gallery.addView(view);
        }

        if(!img4.equals(""))
        {
            View view = inflater.inflate(R.layout.image_item, gallery, false);
            ImageView itemImages = (ImageView)view.findViewById(R.id.images);
            Picasso.with(getApplicationContext()).load(img4).into(itemImages);
            gallery.addView(view);
        }

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void setDescription()
    {
        mDatabase.child("Data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    boolean check1 = snapshot.getValue(Item.class).getTitle().equals(tempTitle);
                    boolean check2 = (snapshot.getValue(Item.class).getPrice() == tempPrice);
                    boolean check3 = snapshot.getValue(Item.class).getCurrentUserUID().equals(tempUid);

                    if(check1 && check2 && check3)
                    {
                        author.setText(snapshot.getValue(Item.class).getAuthor());
                        publisher.setText(snapshot.getValue(Item.class).getPublisher());
                        isbn.setText(snapshot.getValue(Item.class).getISBN());
                        edition.setText(snapshot.getValue(Item.class).getEdition());
                        title.setText(snapshot.getValue(Item.class).getTitle());
                        author2.setText(snapshot.getValue(Item.class).getAuthor());
                        price.setText("$" + snapshot.getValue(Item.class).getPrice());
                        coverImage = snapshot.getValue(Item.class).getImgCoverUrl();
                        img1 = snapshot.getValue(Item.class).getImg1Url();
                        img2 = snapshot.getValue(Item.class).getImg2Url();
                        img3 = snapshot.getValue(Item.class).getImg3Url();
                        img4 = snapshot.getValue(Item.class).getImg4Url();

                        if(!coverImage.equals(""))
                        {
                            Picasso.with(Description_Activity.this).load(coverImage).into(cImg);
                        }

                        mDatabase.child("UserData").addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot2) {
                               for(DataSnapshot snapshot2: dataSnapshot2.getChildren()){
                                   boolean check4 = snapshot2.getValue(Data.class).getUID().equals(tempUid);

                                   if(check4)
                                   {
                                       sellerName.setText(snapshot2.getValue(Data.class).getName());
                                       sellerEmail.setText(snapshot2.getValue(Data.class).getuName());
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

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void onEmail(View view) {
        Intent i = new Intent(Description_Activity.this, EmailActivity.class);
        i.putExtra("sellerEmail", sellerEmail.getText().toString());
        i.putExtra("book", title.getText().toString());
        i.putExtra("Title", tempTitle);
        i.putExtra("Price", tempPrice);
        i.putExtra("UID", tempUid);
        startActivity(i);
    }

    public void onGoBack(View view) {
        Intent i = new Intent(Description_Activity.this, ListActivity.class);
        i.putExtra("Search", isbn.getText().toString());
        i.putExtra("SearchType", "ISBN");
        startActivity(i);
        finish();
    }

    private void setRating(){

        FirebaseDatabase.getInstance().getReference("UserData").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getValue(Data.class).getUID().equals(tempUid)){
                        rate.setRating((float)snapshot.getValue(Data.class).getRating());
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
