package com.example.me.fscbookfinder;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.me.fscbookfinder.model.Item;
import com.example.me.fscbookfinder.model.ListItems;
import com.example.me.fscbookfinder.model.SellingItems;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SellingCartActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<SellingItems> SellingItemList = new ArrayList<>();

    private DatabaseReference myDatabaseRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling_cart);
        toolbarSetUp();
        mAuth = FirebaseAuth.getInstance();

        myDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mRecyclerView = (RecyclerView) findViewById(R.id.selling_books_list);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SellingItemsArrayAdapter(SellingCartActivity.this, SellingItemList);
        mRecyclerView.setAdapter(mAdapter);

        getListOfSellingBooks();
    }

    private void getListOfSellingBooks(){
        final String currentUID = mAuth.getCurrentUser().getUid();

        myDatabaseRef.child("Data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    if(snapshot.getValue(Item.class).getCurrentUserUID().equals(currentUID)){
                        String tempTitle = snapshot.getValue(Item.class).getTitle();
                        double tempPrice = snapshot.getValue(Item.class).getPrice();
                        String tempKey = snapshot.getKey();
                        SellingItems tempItem = new SellingItems(tempTitle, tempPrice, tempKey);
                        SellingItemList.add(tempItem);
                    }
                }
                mAdapter.notifyDataSetChanged();
                toggleEmptyList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void toolbarSetUp() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_home));
    }

    private void toggleEmptyList() {

        if (mAdapter.getItemCount() <= 0) {
            setContentView(R.layout.empty_items);

            TextView back = (TextView)findViewById(R.id.no_result_back_button);
            Toolbar emptyListtoolbar;
            emptyListtoolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(emptyListtoolbar);
            ActionBar actionBar = getSupportActionBar();

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    finish();
                }
            });
        }
    }
}
