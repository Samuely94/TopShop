package com.project.cs371.couponapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class URLFetch implements RateLimit.RateLimitCallback{
    public interface Callback {
        void fetchStart();
        void fetchComplete(String result);
        void fetchCancel(String url);
    }
    protected Callback callback = null;
    protected URL url;

    public URLFetch(Callback callback, URL url, boolean front) {
        this.callback = callback;
        this.url = url;
        if( front ) {
            RateLimit.getInstance().addFront(this);
        } else {
            RateLimit.getInstance().add(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return url.equals(((URLFetch)obj).url);
    }

    public void rateLimitReady() {
        new AsyncDownloader().execute(url);
    }

    public class AsyncDownloader extends AsyncTask<URL, Integer, String> {


        protected String doInBackground(URL... urls) {

            URL url = urls[0];
            HttpURLConnection conection = null;
            String json = "";
            Bitmap mIcon11 = null;
            try {
                conection = (HttpURLConnection)url.openConnection();
                conection.connect();
                String contentType = conection.getHeaderField("Content-Type");
                boolean image = contentType.startsWith("image/");
                if(image ){
                    if(BitmapCache.getInstance().getBitmap(url.toString())==null){
                    try {
                        InputStream in = new java.net.URL(url.toString()).openStream();
                        mIcon11 = BitmapFactory.decodeStream(in);
                        BitmapCache.getInstance().setBitmap(url.toString(),mIcon11);
                        return url.toString();
                    } catch (Exception e) {
                        Log.e("Error", e.getMessage());
                        e.printStackTrace();
                    }
                }}
                else {
                    InputStream stream = conection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    json = sb.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conection != null) {
                    conection.disconnect();
                }
            }

            return json;
        }
        @Override
        protected void onCancelled(String string) {
            callback.fetchCancel(string);

        }

        @Override
        protected void onPostExecute(String string) {
            callback.fetchComplete(string);

        }
        @Override
        protected void onPreExecute() {
            callback.fetchStart();
        }
    }

    }

