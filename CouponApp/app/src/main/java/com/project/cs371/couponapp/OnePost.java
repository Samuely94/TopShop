package com.project.cs371.couponapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;

public class OnePost extends AppCompatActivity implements URLFetch.Callback{
    ImageView imageview;
    TextView textview3;
    TextView textview2;
    TextView textview;
    Button attachButton;

    String mess;

        public void fetchStart(){
            // Toast.makeText(getBaseContext(), "Image fetch started.", Toast.LENGTH_SHORT).show();
        }
        public void fetchComplete(String string){
            Bitmap bit = BitmapCache.getInstance().getBitmap(mess);
            imageview.setImageBitmap(bit);
        }
        public void fetchCancel(String string){
        }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_post);
        textview = (TextView)findViewById(R.id.textView);
        imageview = (ImageView)findViewById(R.id.imageView);
        textview2 = (TextView)findViewById(R.id.textView2);
        textview3 = (TextView)findViewById(R.id.textView3);
        attachButton = (Button) findViewById(R.id.attachButton);
        final Bundle extras = getIntent().getExtras();
        mess = extras.getString("image");
        textview.setText( extras.getString("title"));
        textview2.setText( extras.getString("description").replaceAll("\\<[^>]*>",""));
        textview3.setText(extras.getString("url"));
        try {
            URL url = new URL(mess);
            OnePost fetchCallback = this;
            URLFetch fetch = new URLFetch(fetchCallback, url,true);
            RateLimit.getInstance().add((RateLimit.RateLimitCallback) fetch);
        }
        catch (Exception e) {
            Log.d("Error: ", mess);
        };
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.myCoupons.put(SearchActivity.itemName, extras);
            }
        });

    }



    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
