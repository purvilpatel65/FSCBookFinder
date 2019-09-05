package com.example.me.fscbookfinder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.me.fscbookfinder.model.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends AppCompatActivity {

    private EditText Author, Title, Isbn, Publisher, EDITION, price;
    private ImageView coverImg;
    private Button AddBtn;
    private TextView goBack;
    private FloatingActionButton btnImg1, btnImg2, btnImg3, btnImg4, btnCI;
    private ImageView img1, img2, img3, img4;
    private Bitmap bitmap1=null, bitmap2=null, bitmap3=null, bitmap4=null, bitmapCover=null;
    private String img1Name = "", img2Name = "", img3Name = "", img4Name = "", imgCoverName = "";
    private String currentUID, getKey, uploadId="";
    private Toolbar toolbar;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    private String url1 = "", url2 = "", url3 = "", url4 = "", urlCover = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Author = (EditText) findViewById(R.id.authorTxtBox);
        Title = (EditText) findViewById(R.id.titleTxtBox);
        Isbn = (EditText) findViewById(R.id.isbnTxtBox);
        Publisher = (EditText) findViewById(R.id.publisherTxtBox);
        EDITION = (EditText) findViewById(R.id.editionTxtBox);
        price = (EditText) findViewById(R.id.priceTxtBox);
        coverImg = (ImageView) findViewById(R.id.coverImg);
        AddBtn = (Button) findViewById(R.id.addBtn);
        goBack = (TextView)findViewById(R.id.backBtn);

        btnImg1 = findViewById(R.id.btnImg1);
        btnImg2 = findViewById(R.id.btnImg2);
        btnImg3 = findViewById(R.id.btnImg3);
        btnImg4 = findViewById(R.id.btnImg4);
        btnCI = findViewById(R.id.btnCI);

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        img4 = (ImageView) findViewById(R.id.img4);


        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Data");
        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();
        toolbarSetUp();
        getKey= "";

        final Intent getIntent = getIntent();

        if (getIntent.hasExtra("author"))
            Author.setText(getIntent.getStringExtra("author"));
        if (getIntent.hasExtra("ISBN"))
            Isbn.setText(getIntent.getStringExtra("ISBN"));
        if (getIntent.hasExtra("publisher"))
            Publisher.setText(getIntent.getStringExtra("publisher"));
        if (getIntent.hasExtra("title"))
            Title.setText(getIntent.getStringExtra("title"));
        if (getIntent.hasExtra("Edition"))
            EDITION.setText(getIntent.getStringExtra("Edition"));
        if (getIntent.hasExtra("byteArray")) {

            bitmapCover = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"), 0, getIntent().getByteArrayExtra("byteArray").length);
            coverImg.setImageBitmap(bitmapCover);
            btnCI.setVisibility(View.GONE);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
            String timeStamp = dateFormat.format(new Date());
            imgCoverName = "picture_" + currentUID + timeStamp + ".jpg";
        }
        if(getIntent.hasExtra("KEY"))
            getKey = getIntent.getStringExtra("KEY");

        if(!getKey.equals(""))
            findInfo(getKey);

        imagesSetUp();
        settingOnLongClickListenerOnImages();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!getKey.equals("")){
                    startActivity(new Intent(AddActivity.this, SellingCartActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(AddActivity.this, ScanActivity.class));
                    finish();

                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 11 && data!=null) {

            bitmap1 = (Bitmap) data.getExtras().get("data");
            img1.setImageBitmap(bitmap1);
            btnImg1.setVisibility(View.GONE);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
            String timeStamp = dateFormat.format(new Date());
            img1Name = "picture_" + currentUID + timeStamp + ".jpg";


        }

        if (requestCode == 12 && data.getExtras()!=null) {
            bitmap2 = (Bitmap) data.getExtras().get("data");
            img2.setImageBitmap(bitmap2);
            btnImg2.setVisibility(View.GONE);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
            String timeStamp = dateFormat.format(new Date());
            img2Name = "picture_" + currentUID + timeStamp + ".jpg";

        }

        if (requestCode == 13 && data.getExtras()!=null) {
            bitmap3 = (Bitmap) data.getExtras().get("data");
            img3.setImageBitmap(bitmap3);
            btnImg3.setVisibility(View.GONE);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
            String timeStamp = dateFormat.format(new Date());
            img3Name = "picture_" + currentUID + timeStamp + ".jpg";

        }

        if (requestCode == 14 && data.getExtras()!=null) {
            bitmap4 = (Bitmap) data.getExtras().get("data");
            img4.setImageBitmap(bitmap4);
            btnImg4.setVisibility(View.GONE);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
            String timeStamp = dateFormat.format(new Date());
            img4Name = "picture_" + currentUID + timeStamp + ".jpg";

        }

        if (requestCode == 15 && data.getExtras()!=null) {
            bitmapCover = (Bitmap) data.getExtras().get("data");
            coverImg.setImageBitmap(bitmapCover);
            btnCI.setVisibility(View.GONE);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
            String timeStamp = dateFormat.format(new Date());
            imgCoverName = "picture_" + currentUID + timeStamp + ".jpg";

        }
    }

    private void imagesSetUp() {
        btnImg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 11);
            }
        });

        btnImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 12);
            }
        });

        btnImg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 13);
            }
        });

        btnImg4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 14);
            }
        });

        btnCI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 15);
            }
        });
    }

    //
    public void onAdd(View view) {
        boolean checkData = validateEntry();

        if (checkData == true) {

            if(getKey.equals(""))
                uploadId = mDatabaseRef.push().getKey();
            else
                uploadId = getKey;

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Uploading...");
            pd.setMessage("Please Wait.");
            pd.setCancelable(false);
            pd.show();

            if (bitmap1 != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imgdata = baos.toByteArray();

                StorageReference fileToUpload = mStorageRef.child(img1Name);


                fileToUpload.putBytes(imgdata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        url1 = taskSnapshot.getDownloadUrl().toString();

                        Item item = new Item(Title.getText().toString().trim(), Author.getText().toString().trim(), Isbn.getText().toString().trim(), Publisher.getText().toString().trim(), EDITION.getText().toString().trim(), Double.parseDouble(price.getText().toString().trim()), urlCover, url1, url2, url3, url4, currentUID);
                        mDatabaseRef.child(uploadId).setValue(item);

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }

            if (bitmap2 != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imgdata = baos.toByteArray();

                StorageReference fileToUpload = mStorageRef.child(img2Name);


                fileToUpload.putBytes(imgdata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        url2 = taskSnapshot.getDownloadUrl().toString();

                        Item item = new Item(Title.getText().toString().trim(), Author.getText().toString().trim(), Isbn.getText().toString().trim(), Publisher.getText().toString().trim(), EDITION.getText().toString().trim(), Double.parseDouble(price.getText().toString().trim()), urlCover, url1, url2, url3, url4, currentUID);

                        mDatabaseRef.child(uploadId).setValue(item);

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }

            if (bitmap3 != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap3.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imgdata = baos.toByteArray();

                StorageReference fileToUpload = mStorageRef.child(img3Name);


                fileToUpload.putBytes(imgdata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        url3 = taskSnapshot.getDownloadUrl().toString();

                        Item item = new Item(Title.getText().toString().trim(), Author.getText().toString().trim(), Isbn.getText().toString().trim(), Publisher.getText().toString().trim(), EDITION.getText().toString().trim(), Double.parseDouble(price.getText().toString().trim()), urlCover, url1, url2, url3, url4, currentUID);

                        mDatabaseRef.child(uploadId).setValue(item);

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }

            if (bitmap4 != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap4.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imgdata = baos.toByteArray();

                StorageReference fileToUpload = mStorageRef.child(img4Name);


                fileToUpload.putBytes(imgdata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        url4 = taskSnapshot.getDownloadUrl().toString();

                        Item item = new Item(Title.getText().toString().trim(), Author.getText().toString().trim(), Isbn.getText().toString().trim(), Publisher.getText().toString().trim(), EDITION.getText().toString().trim(), Double.parseDouble(price.getText().toString().trim()), urlCover, url1, url2, url3, url4, currentUID);

                        mDatabaseRef.child(uploadId).setValue(item);

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }

            if (bitmapCover != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmapCover.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imgdata = baos.toByteArray();

                StorageReference fileToUpload = mStorageRef.child(imgCoverName);


                fileToUpload.putBytes(imgdata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        urlCover = taskSnapshot.getDownloadUrl().toString();

                        Item item = new Item(Title.getText().toString().trim(), Author.getText().toString().trim(), Isbn.getText().toString().trim(), Publisher.getText().toString().trim(), EDITION.getText().toString().trim(), Double.parseDouble(price.getText().toString().trim()), urlCover, url1, url2, url3, url4, currentUID);

                        mDatabaseRef.child(uploadId).setValue(item);

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }

            if(bitmapCover==null && bitmap1==null && bitmap2==null && bitmap3==null && bitmap4==null){
                Item item = new Item(Title.getText().toString().trim(), Author.getText().toString().trim(), Isbn.getText().toString().trim(), Publisher.getText().toString().trim(), EDITION.getText().toString().trim(), Double.parseDouble(price.getText().toString().trim()), urlCover, url1, url2, url3, url4, currentUID);

                mDatabaseRef.child(uploadId).setValue(item);
            }

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(pd.isShowing()) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_SHORT).show();
                        afterAdd();
                    }
                }
            }, 11000);


        }

    }


    private void toolbarSetUp() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_home));
    }

    private boolean validateEntry() {
        boolean check = true;

        if (Author.getText().toString().trim().equals("")) {
            Author.setError("Invalid Entry");
            check = false;
        }

        if (Isbn.getText().toString().trim().equals("") || (Isbn.getText().toString().trim().length() != 13)) {
            if (Isbn.getText().toString().trim().length() != 13) {
                Isbn.setError("ISBN must be 13 digits!");
                check = false;
            } else {
                Isbn.setError("Invalid Entry");
                check = false;
            }
        }

        if (Title.getText().toString().trim().equals("")) {
            Title.setError("Invalid Entry");
            check = false;
        }

        if (price.getText().toString().trim().equals("")) {
            price.setError("Invalid Entry");
            check = false;
        }

        return check;
    }

    private void afterAdd() {
        Author.setText("");
        Title.setText("");
        Isbn.setText("");
        Publisher.setText("");
        price.setText("");
        EDITION.setText("");

        coverImg.setImageBitmap(null);
        btnCI.setVisibility(View.VISIBLE);

        img1.setImageBitmap(null);
        btnImg1.setVisibility(View.VISIBLE);

        img2.setImageBitmap(null);
        btnImg2.setVisibility(View.VISIBLE);

        img3.setImageBitmap(null);
        btnImg3.setVisibility(View.VISIBLE);

        img4.setImageBitmap(null);
        btnImg4.setVisibility(View.VISIBLE);

}

private void resetImageAlert(final ImageView view, final FloatingActionButton button, final String url, final boolean checkIfImageExists)
{
    CharSequence colors[] = new CharSequence[]{"Reset"};

    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setItems(colors, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i==0)
            {
                if(view!=null) {
                    if (checkIfImageExists == true) {
                        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                        photoRef.delete().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        if(url.equals(urlCover)) urlCover="";
                        if(url.equals(url1)) url1="";
                        if(url.equals(url2)) url2="";
                        if(url.equals(url3)) url3="";
                        if(url.equals(url4)) url4="";
                    }
                    view.setImageBitmap(null);
                    button.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Sorry! View cannot be reset because it has no image assigned to it!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    });
    builder.show();
}

private void settingOnLongClickListenerOnImages()
{
   coverImg.setOnLongClickListener(new View.OnLongClickListener() {
       @Override
       public boolean onLongClick(View view) {

           if(!getKey.equals(""))
               resetImageAlert(coverImg, btnCI, urlCover, true);
           else
               resetImageAlert(coverImg, btnCI, urlCover, false);



           return true;
       }
   });

    img1.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {

            if(!getKey.equals(""))
                 resetImageAlert(img1, btnImg1, url1, true);
            else
                resetImageAlert(img1, btnImg1, url1, false);


            return false;
        }
    });

    img2.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {

            if(!getKey.equals(""))
                resetImageAlert(img2, btnImg2, url2, true);
            else
                resetImageAlert(img2, btnImg2, url2, false);


            return false;
        }
    });

    img3.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {

            if(!getKey.equals(""))
                resetImageAlert(img3, btnImg3, url3, true);
            else
                resetImageAlert(img3, btnImg3, url3, false);


            return false;
        }
    });

    img4.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {

            if(!getKey.equals(""))
                resetImageAlert(img4, btnImg4, url4, true);
            else
                resetImageAlert(img4, btnImg4, url4, false);


            return false;
        }
    });

}

    private void findInfo(final String key){
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    if(snapshot.getKey().equals(key)){

                        Title.setText(snapshot.getValue(Item.class).getTitle());
                        Author.setText(snapshot.getValue(Item.class).getAuthor());
                        Isbn.setText(snapshot.getValue(Item.class).getISBN());
                        Publisher.setText(snapshot.getValue(Item.class).getPublisher());
                        EDITION.setText(snapshot.getValue(Item.class).getEdition());
                        price.setText(Double.toString(snapshot.getValue(Item.class).getPrice()));

                        if(!snapshot.getValue(Item.class).getImgCoverUrl().equals(""))
                        {
                            urlCover = snapshot.getValue(Item.class).getImgCoverUrl();
                            btnCI.setVisibility(View.GONE);
                            Picasso.with(AddActivity.this).load(snapshot.getValue(Item.class).getImgCoverUrl()).into(coverImg);
                        }
                        if(!snapshot.getValue(Item.class).getImg1Url().equals(""))
                        {
                            url1 = snapshot.getValue(Item.class).getImg1Url();
                            btnImg1.setVisibility(View.GONE);
                            Picasso.with(AddActivity.this).load(snapshot.getValue(Item.class).getImg1Url()).into(img1);
                        }
                        if(!snapshot.getValue(Item.class).getImg2Url().equals(""))
                        {
                            url2 = snapshot.getValue(Item.class).getImg2Url();
                            btnImg2.setVisibility(View.GONE);
                            Picasso.with(AddActivity.this).load(snapshot.getValue(Item.class).getImg2Url()).into(img2);
                        }
                        if(!snapshot.getValue(Item.class).getImg3Url().equals(""))
                        {
                            url3 = snapshot.getValue(Item.class).getImg3Url();
                            btnImg3.setVisibility(View.GONE);
                            Picasso.with(AddActivity.this).load(snapshot.getValue(Item.class).getImg3Url()).into(img3);
                        }
                        if(!snapshot.getValue(Item.class).getImg4Url().equals(""))
                        {
                            url4 = snapshot.getValue(Item.class).getImg4Url();
                            btnImg4.setVisibility(View.GONE);
                            Picasso.with(AddActivity.this).load(snapshot.getValue(Item.class).getImg4Url()).into(img4);
                        }
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


