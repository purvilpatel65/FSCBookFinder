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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListActivity extends AppCompatActivity {



    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<ListItems> itemList = new ArrayList<>();
    private Toolbar toolbar;

    private DatabaseReference myDatabaseRef;
    private String search, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        toolbarSetUp();

        Intent getIntent = getIntent();

        if(getIntent.hasExtra("Search"))
            search = getIntent.getStringExtra("Search");
        if(getIntent.hasExtra("SearchType"))
            type = getIntent.getStringExtra("SearchType");

        myDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mRecyclerView = (RecyclerView) findViewById(R.id.books_list);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ItemArrayAdapter(getBaseContext(), itemList);
        mRecyclerView.setAdapter(mAdapter);

        addList();
    }

    protected void addList()
    {
        if(type.equals("ISBN"))
            doByIsbn();
        else if(type.equals("Title"))
            doByTitle();
    }

    protected void doByIsbn(){

        myDatabaseRef.child("Data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    if (snapshot.getValue(Item.class).getISBN().equalsIgnoreCase(search)  ) {
                        String tempTitle = snapshot.getValue(Item.class).getTitle();
                        double tempPrice = snapshot.getValue(Item.class).getPrice();
                        String tempUID = snapshot.getValue(Item.class).getCurrentUserUID();
                        ListItems tempItem = new ListItems(tempTitle, tempPrice, tempUID);
                        itemList.add(tempItem);

                    }
                }
                Collections.sort(itemList, new Comparator<ListItems>() {
                    @Override
                    public int compare(ListItems c1, ListItems c2) {
                        return Double.compare(c1.getPriceOfBook(), c2.getPriceOfBook());
                    }
                });
                mAdapter.notifyDataSetChanged();
                toggleEmptyList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });


    }

    protected void doByTitle(){
        myDatabaseRef.child("Data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    String tempStrValue = snapshot.getValue(Item.class).getTitle().toLowerCase();
                    String tempSearch = search.toLowerCase();
                    if (snapshot.getValue(Item.class).getTitle().equalsIgnoreCase(search) || tempStrValue.contains(tempSearch)) {
                        String tempTitle = snapshot.getValue(Item.class).getTitle();
                        double tempPrice = snapshot.getValue(Item.class).getPrice();
                        String tempUID = snapshot.getValue(Item.class).getCurrentUserUID();
                        ListItems tempItem = new ListItems(tempTitle, tempPrice, tempUID);
                        itemList.add(tempItem);

                    }
                }
                Collections.sort(itemList, new Comparator<ListItems>() {
                    @Override
                    public int compare(ListItems c1, ListItems c2) {
                        return Double.compare(c1.getPriceOfBook(), c2.getPriceOfBook());
                    }
                });
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
