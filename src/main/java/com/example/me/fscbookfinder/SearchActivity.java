package com.example.me.fscbookfinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class SearchActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private TextView sell;
    private TextView bookQuote;
    private EditText searchBox;
    private RadioGroup rg;
    private RadioButton selectedRadioButton, rb1, rb2;
    private Button searchBtn;
    private String scanBySearchISBN;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBox = (EditText)findViewById(R.id.searchbox);
        rg = (RadioGroup)findViewById(R.id.radioGroup);
        searchBtn = (Button)findViewById(R.id.searchBtn);
        rb1 = (RadioButton)findViewById(R.id.isbnRadio);
        rb2 = (RadioButton)findViewById(R.id.titleRadio);
        sell = (TextView) findViewById(R.id.sellTxt);
        bookQuote = (TextView)findViewById(R.id.book_quote);
        scanBySearchISBN = "";
        mAuth = FirebaseAuth.getInstance();
        bookQuote.setText("\"So many books, so little time.\"");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        if(menuItem.getItemId() == R.id.signOut)
                        {
                            mAuth.signOut();
                            startActivity(new Intent(getApplicationContext(), LogInActivity.class));
                        }

                        if(menuItem.getItemId() == R.id.sellingCart)
                        {
                            startActivity(new Intent(getApplicationContext(), SellingCartActivity.class));
                            finish();
                        }

                        if(menuItem.getItemId() == R.id.rate)
                        {
                            startActivity(new Intent(getApplicationContext(), RateActivity.class));
                            finish();
                        }

                        if(menuItem.getItemId() == R.id.account)
                        {
                            startActivity(new Intent(getApplicationContext(), MyAccountActivity.class));
                            finish();
                        }


                        return true;
                    }
                });

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ScanActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSearch(View view)
    {
        if(!searchBox.getText().toString().equals("")) {
            String whatKindOfSearch = "";
            if (rg.getCheckedRadioButtonId() != -1) {
                selectedRadioButton = (RadioButton) findViewById(rg.getCheckedRadioButtonId());

                if (selectedRadioButton.getId() == rb1.getId())
                    whatKindOfSearch = "ISBN";
                else if (selectedRadioButton.getId() == rb2.getId())
                    whatKindOfSearch = "Title";

                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                intent.putExtra("Search", searchBox.getText().toString().trim());
                intent.putExtra("SearchType", whatKindOfSearch);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Please select the search type (Title or ISBN)", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            searchBox.setError("Title Name or ISBN required!");
        }


    }

    public void onScanForISBN(View view) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
           //we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();

            if(scanContent!=null && scanFormat!=null && scanFormat.equalsIgnoreCase("EAN_13")) {
                scanBySearchISBN = scanContent;
                onPostExecute(scanBySearchISBN);
            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Not a valid scan!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private void onPostExecute(String isbn){
        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        intent.putExtra("Search", isbn);
        intent.putExtra("SearchType", "ISBN");
        startActivity(intent);
        finish();
    }

}
