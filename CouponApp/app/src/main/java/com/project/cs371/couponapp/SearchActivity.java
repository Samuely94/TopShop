package com.project.cs371.couponapp;

import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


public class SearchActivity extends AppCompatActivity implements URLFetch.Callback {
    static public String AppName = "RedFetch";
    static InputStream is = null;
    private String jsonfetch = "";
    private JSONObject jO ;
    private ArrayList<CouponRecord> rRecords;
    private RecyclerView recyclerView;
    private EditText editText;
    private EditText editText2;
    String json;
    private boolean first;
    Button button;
    private ProgressBar mProgress;
    protected final int maxCouponRecords = 100;
    protected DynamicSearchAdapter CouponRecordAdapter = null;
    protected LinearLayoutManager rv_layout_mgr;
    int lastVisible =0;
    int currentVisible=0;
    public static String itemName;
    public void fetchStart(){


    }
    public void fetchComplete(String string){
        if(first) {
            mProgress.setVisibility(View.INVISIBLE);
            jsonfetch = string;
            parseJson();
            CouponRecordAdapter.setRecordSize();
            CouponRecordAdapter.notifyDataSetChanged();
            first = false;
        }
    }
    public void fetchCancel(String string){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // XXX write me: call setContentView
        setContentView(R.layout.search_results);
        // XXX other initialization.
        recyclerView = (RecyclerView) findViewById(R.id.searchResults);
        rRecords = new ArrayList<CouponRecord>();
        CouponRecordAdapter = new DynamicSearchAdapter(rRecords);
        //final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_layout_mgr = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(rv_layout_mgr);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(CouponRecordAdapter);
        mProgress = (ProgressBar) findViewById(R.id.netIndicator);
        Bundle extras = getIntent().getExtras();
        itemName = extras.getString("ItemName");


        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        BitmapCache.cacheSize = maxMemory / 4;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        BitmapCache.maxW = size.x;
        BitmapCache.maxH = size.y;
        editText = (EditText) findViewById(R.id.searchTerm);

        editText2 = (EditText) findViewById(R.id.searchTerm2);
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                first = true;
                newSearch();
            }
        });

        BitmapCache.getInstance().setBitmap("def",BitmapFactory.decodeResource(getResources(),
                R.drawable.image));
    }

    protected void newSearch() {
        CouponRecordAdapter.notifyDataSetChanged();
        first = true;
        if(first)
            mProgress.setVisibility(View.VISIBLE);
        String search = editText.getText().toString();
        String search2 = editText2.getText().toString();


        search = search.replaceAll("\\s+","");
        boolean check = false;
        ArrayList<String> strings = new ArrayList<>(Arrays.asList("&query=", "&location=", "&radius=", "&online=", "&category_slugs=", "&provider_slugs=", "&page=", "&per_page=", "&order="));
        for(String temp : strings) {
            if(search.toLowerCase().contains(temp.toLowerCase())){
                check = true;
            }
        }
        Log.d("", "newSearch: " + search);
        if( !search.equals("&")  ) {
            if(search.equals("")){
                search = "https://api.sqoot.com/v2/deals?api_key=n8zfo9&query=" +search;

            }
            else {
                search = "https://api.sqoot.com/v2/deals?api_key=n8zfo9&query=" +search + "&location=" +search2;
            }

            try {
                Log.d("r", "newSearch: " + search);
                URL url = new URL(search);
                SearchActivity fetchCallback = this;
                URLFetch fetch = new URLFetch(fetchCallback, url, true);
                RateLimit.getInstance().add((RateLimit.RateLimitCallback) fetch);
            } catch (Exception e) {
                Log.d("Error: ", search);
            }
        }
        else {
            Toast.makeText(getBaseContext(), "Search term invalid", Toast.LENGTH_SHORT).show();
            mProgress.setVisibility(View.INVISIBLE);
        }

    }

    public void parseJson(){
        rRecords.clear();
        try{
            jO = new JSONObject(jsonfetch);
            Log.d("what", "parseJson: " + jO.toString());
            JSONArray jA = jO.getJSONArray("deals");
            //jO = jA.getJSONObject(1);
            //Log.d("what2", "parseJson: " + jO.toString());
        } catch( Exception e) {
            e.printStackTrace();
        }
        int jsonIndex = 0;
        try {

            JSONArray jA = jO.getJSONArray("deals");
            while( jsonIndex < jA.length() ) {
                jO = jA.getJSONObject(jsonIndex);
                if( jO.isNull("deal") ){
                    jsonIndex++;continue;
                }
                jO = jO.getJSONObject("deal");
                if( jO.isNull("title")) {
                    jsonIndex++;continue;
                }
                String title = jO.getString("title");
                String description = jO.getString("description");
                String thumbnailURL = jO.getString("image_url");
                String imageURL = jO.getString("image_url");
                String url = jO.getString("untracked_url");
                CouponRecord redd = new CouponRecord();
                redd.title = title;
                redd.url = url;
                redd.description = description;
                redd.imageURL = new URL (thumbnailURL + "&geometry=400x");
                redd.thumbnailURL = new URL (imageURL+"&geometry=200x");
                rRecords.add(redd);
                jsonIndex++;
            }
        } catch( Exception e) {
            e.printStackTrace();
        }
    }





/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings :
                return true;
            case R.id.exit:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    */
}

