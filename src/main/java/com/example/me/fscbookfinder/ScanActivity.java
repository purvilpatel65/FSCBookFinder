package com.example.me.fscbookfinder;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ScanActivity extends AppCompatActivity {

    private Button scanBtn, manualBtn;
    private Bitmap thumbImg;
    public String author, title, ISBN, publisher, Edition;
    private AdView adView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        scanBtn = (Button)findViewById(R.id.searchScan);
        manualBtn = (Button)findViewById(R.id.searchManual);
        toolbarSetUp();

        MobileAds.initialize(this, "ca-app-pub-7138221836687138~6864430718");
        adView = (AdView)findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(request);

        manualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddActivity.class);
                startActivity(i);
                finish();
            }
        });


    }

    public void onStartScanning(View view) {
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

            if(scanContent!=null && scanFormat!=null && scanFormat.equalsIgnoreCase("EAN_13")){
                //book search

                ISBN = scanContent;

                String bookSearchString = "https://www.googleapis.com/books/v1/volumes?"+
                        "q=isbn:"+scanContent+"&key=AIzaSyAhbU7lGfTkNrL0m9Q3yTHkrzO-NVzLrfw";

                new GetBookInfo().execute(bookSearchString);

            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Not a valid scan!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private class GetBookInfo extends AsyncTask<String, Void, String> {
        //fetch book info
        @Override
        protected String doInBackground(String... bookURLs) {
        //request book info
            StringBuilder bookBuilder = new StringBuilder();

            for (String bookSearchURL : bookURLs) {
              //search urls
                HttpClient bookClient = new DefaultHttpClient();
                try {
                    //get the data
                    HttpGet bookGet = new HttpGet(bookSearchURL);
                    HttpResponse bookResponse = bookClient.execute(bookGet);

                    StatusLine bookSearchStatus = bookResponse.getStatusLine();
                    if (bookSearchStatus.getStatusCode()==200) {
                        //we have a result
                        HttpEntity bookEntity = bookResponse.getEntity();

                        InputStream bookContent = bookEntity.getContent();
                        InputStreamReader bookInput = new InputStreamReader(bookContent);
                        BufferedReader bookReader = new BufferedReader(bookInput);

                        String lineIn;
                        while ((lineIn=bookReader.readLine())!=null) {
                            bookBuilder.append(lineIn);
                        }
                    }
                }
                catch(Exception e){ e.printStackTrace(); }
            }

            return bookBuilder.toString();
        }

        protected void onPostExecute(String result) {
           //parse search results
            try{
                  //parse results

                JSONObject resultObject = new JSONObject(result);
                JSONArray bookArray = resultObject.getJSONArray("items");

                JSONObject bookObject = bookArray.getJSONObject(0);

                JSONObject volumeObject = bookObject.getJSONObject("volumeInfo");

                try{publisher = volumeObject.getString("publisher");}
                catch (JSONException jse){
                    jse.printStackTrace();
                }

                try{

                    title = volumeObject.getString("title");
                } //here
                catch(JSONException jse){
                    title = "";
                    jse.printStackTrace();
                }

                StringBuilder authorBuild = new StringBuilder("");
                try{
                    JSONArray authorArray = volumeObject.getJSONArray("authors");  //here
                    for(int a=0; a<authorArray.length(); a++){
                        if(a>0) authorBuild.append(", ");
                        authorBuild.append(authorArray.getString(a));
                    }
                    author = authorBuild.toString();
                }
                catch(JSONException jse){
                    author = "";
                    jse.printStackTrace();
                }


                try{

                    Edition = volumeObject.getString("edition");
                } //here
                catch(JSONException jse){
                      Edition = "";
                    jse.printStackTrace();
                }

                try{
                    JSONObject imageInfo = volumeObject.getJSONObject("imageLinks");
                    new GetBookThumb().execute(imageInfo.getString("smallThumbnail"));
                }
                catch(JSONException jse){
                    jse.printStackTrace();
                }
            }
            catch (Exception e) {
                 //no result
                e.printStackTrace();
               author = "";
               title = "";
               publisher = "";
               Edition = "";
               ISBN = "";
               thumbImg = null;
            }
        }
    }

    private class GetBookThumb extends AsyncTask<String, Void, String> {
       //get thumbnail

        @Override
        protected String doInBackground(String... thumbURLs) {
            //attempt to download image
            try{
               //try to download
                URL thumbURL = new URL(thumbURLs[0]);
                URLConnection thumbConn = thumbURL.openConnection();
                thumbConn.connect();

                InputStream thumbIn = thumbConn.getInputStream();
                BufferedInputStream thumbBuff = new BufferedInputStream(thumbIn);

                thumbImg = BitmapFactory.decodeStream(thumbBuff);


                thumbBuff.close();
                thumbIn.close();
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        protected void onPostExecute(String result) {
            //thumbView.setImageBitmap(thumbImg);
            Intent intent2 = new Intent(getApplicationContext(), AddActivity.class);
            intent2.putExtra("author", author);
            intent2.putExtra("ISBN", ISBN);
            intent2.putExtra("publisher", publisher);
            intent2.putExtra("title", title);
            intent2.putExtra("Edition", Edition);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                thumbImg.compress(Bitmap.CompressFormat.PNG, 100, bs);
                intent2.putExtra("byteArray", bs.toByteArray());
            startActivity(intent2);
            finish();
        }

}

    private void toolbarSetUp()
    {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_home));
    }
}
