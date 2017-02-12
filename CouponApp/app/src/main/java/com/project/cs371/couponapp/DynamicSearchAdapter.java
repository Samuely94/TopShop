package com.project.cs371.couponapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

/**
 * Created by ajuarez on 10/9/2016.
 */

public class DynamicSearchAdapter extends  RecyclerView.Adapter<DynamicSearchAdapter.DynamicViewHolder> {
    public ArrayList<CouponRecord> Records;
    public Context context;
    public int currentSize;
    public RecyclerView mRecyclerView;
    public boolean swipe = false;
    public long starttime = 0;
    public class ImageFetchCallback implements URLFetch.Callback {
        public void fetchStart(){
        }
        public void fetchComplete(String string){
        notifyDataSetChanged();
        }
        public void fetchCancel(String string){
        }
    }

    public class DynamicViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView title;
        public View container;
        public DynamicViewHolder(View view) {
            super(view);
            context= view.getContext();
            container = view;
            thumbnail = (ImageView) view.findViewById(R.id.picTextRowPic);
            title = (TextView) view.findViewById(R.id.picTextRowText);
        }
    }

    public DynamicSearchAdapter(ArrayList<CouponRecord> Records) {
        this.Records = Records;
    }


    public void addRecord(CouponRecord redd){
        Records.add(redd);
    }

    @Override
    public DynamicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pic_text_row, parent, false);
        return new DynamicViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DynamicViewHolder holder, final int position) {
        CouponRecord record = Records.get(position);
        holder.title.setText(record.title);
        if(BitmapCache.getInstance().getBitmap(record.thumbnailURL.toString())==null) {
            holder.thumbnail.setImageBitmap(BitmapCache.getInstance().getBitmap("def"));
            try {
                URL url = record.thumbnailURL;
                ImageFetchCallback fetchCallback = new ImageFetchCallback();
                URLFetch fetch = new URLFetch(fetchCallback, url, false);
                //RateLimit.getInstance().add(fetch);
            } catch (Exception e) {
                Log.d("Error: ", "thumbs" + record.thumbnailURL);
            }
        }
        else
            holder.thumbnail.setImageBitmap(BitmapCache.getInstance().getBitmap(record.thumbnailURL.toString()));
        starttime = 0;
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(context, OnePost.class);
                intent.putExtra("image", Records.get(position).imageURL.toString());
                intent.putExtra("title", Records.get(position).title);
                intent.putExtra("description", Records.get(position).description);
                intent.putExtra("url", Records.get(position).url);
                    Log.d("y", "onClick: " + Records.get(position).url);
                context.startActivity(intent);


            }
        });

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }


    public void setTime(long time){
        this.starttime = time;
    }

    public void setRecordSize(){
        this.currentSize = Records.size();
    }

    @Override
    public int getItemCount() {
        return Records.size();
    }
}
