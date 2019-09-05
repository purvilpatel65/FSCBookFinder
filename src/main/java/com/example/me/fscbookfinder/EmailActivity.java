package com.example.me.fscbookfinder;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EmailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText recipient;
    private EditText subject;
    private EditText body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        toolbarSetUp();

        recipient = (EditText) findViewById(R.id.recipient);
        subject = (EditText) findViewById(R.id.subject);
        body = (EditText) findViewById(R.id.body);
        Button sendBtn = (Button) findViewById(R.id.sendEmail);
        Button goBack = (Button)findViewById(R.id.goBackBtn);

        Intent getIntent = getIntent();
        if(getIntent.hasExtra("sellerEmail"))
            recipient.setText(getIntent.getStringExtra("sellerEmail"));
        if(getIntent.hasExtra("book"))
        {
            String tempSubject = "About trading for book " + (getIntent.getStringExtra("book"));
            subject.setText(tempSubject);
        }
        final String tempTitle = getIntent.getStringExtra("Title");
        final String tempUid = getIntent.getStringExtra("UID");
        final double tempPrice = getIntent.getDoubleExtra("Price", 0);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if(recipient.getText().toString().trim().equals(""))
                {
                    recipient.setError("Recipient email address is required!");
                }
                else {
                    sendEmail();
                    // after sending the email, clear the fields
                    recipient.setText("");
                    subject.setText("");
                    body.setText("");
                }
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EmailActivity.this, Description_Activity.class);
                i.putExtra("Title", tempTitle);
                i.putExtra("Price", tempPrice);
                i.putExtra("UID", tempUid);
                startActivity(i);
                finish();
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

    protected void sendEmail() {
        String[] recipients = {recipient.getText().toString()};
        Intent email = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));

        // prompts email clients only
        email.setType("message/rfc822");

        email.putExtra(Intent.EXTRA_EMAIL, recipients);
        email.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString());
        email.putExtra(Intent.EXTRA_TEXT, body.getText().toString());

        try {
            // the user can choose the email client
            startActivity(Intent.createChooser(email, "Choose an email client from..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(EmailActivity.this, "No email client installed.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
