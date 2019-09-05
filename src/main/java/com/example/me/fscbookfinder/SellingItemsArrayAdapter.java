package com.example.me.fscbookfinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.me.fscbookfinder.model.Item;
import com.example.me.fscbookfinder.model.SellingItems;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class SellingItemsArrayAdapter extends RecyclerView.Adapter<SellingItemsArrayAdapter.ViewHolder> {

    private List<SellingItems> items;
    Context _context;

    public SellingItemsArrayAdapter(Context context, List<SellingItems> books) {
        items = books;
        _context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView bookName;
        public TextView price;
        public Button edit, delete;


        public ViewHolder(LinearLayout v) {
            super(v);
            bookName = (TextView)v.findViewById(R.id.nameTxt);
            price = (TextView)v.findViewById(R.id.priceTxt);
            edit = (Button)v.findViewById(R.id.editBtn);
            delete = (Button)v.findViewById(R.id.deleteBtn);
        }
    }

    @Override
    public SellingItemsArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.selling_cart_layout, parent, false);
        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;

    }

    @Override
    public void onBindViewHolder(SellingItemsArrayAdapter.ViewHolder holder, final int position) {
        final String tempTitle = items.get(position).getBookName();
        holder.bookName.setText(items.get(position).getBookName());

        final String tempPrice = Double.toString(items.get(position).getPrice());
        final double tempPriceDouble = items.get(position).getPrice();
        holder.price.setText(tempPrice);

        final String Key = items.get(position).getKey();

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(_context, AddActivity.class);
                intent.putExtra("Title", tempTitle);
                intent.putExtra("Price", tempPriceDouble);
                intent.putExtra("KEY", Key);
                _context.startActivity(intent);
                ((Activity)_context).finish();
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(_context);

                builder1.setMessage("Are you sure you want to delete this book?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                onDelete(Key);
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

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void onDelete(final String key){

        final DatabaseReference mDatabaseRef =  FirebaseDatabase.getInstance().getReference("Data");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(key)) {
                        if(!snapshot.getValue(Item.class).getImgCoverUrl().equals(""))
                            deleteImage(snapshot.getValue(Item.class).getImgCoverUrl());
                        if(!snapshot.getValue(Item.class).getImg1Url().equals(""))
                            deleteImage(snapshot.getValue(Item.class).getImg1Url());
                        if(!snapshot.getValue(Item.class).getImg2Url().equals(""))
                            deleteImage(snapshot.getValue(Item.class).getImg2Url());
                        if(!snapshot.getValue(Item.class).getImg3Url().equals(""))
                            deleteImage(snapshot.getValue(Item.class).getImg3Url());
                        if(!snapshot.getValue(Item.class).getImg4Url().equals(""))
                            deleteImage(snapshot.getValue(Item.class).getImg4Url());

                        mDatabaseRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(_context, "Book successfully deleted!\nNow, you will be taken to home to refresh the database. Thank You!", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(_context, SearchActivity.class);
                                _context.startActivity(i);
                                ((Activity)_context).finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(_context, "Deletion request failed! please try again.", Toast.LENGTH_LONG).show();
                            }
                        });

                        break;
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(_context, databaseError.getMessage().toString(), Toast.LENGTH_LONG);
            }
        });

    }

    private void deleteImage(String url)
    {
        FirebaseStorage.getInstance().getReferenceFromUrl(url).delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(_context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
