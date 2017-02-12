package com.project.cs371.couponapp;

/**
 * Created by Samuel on 11/9/2016.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import java.net.URL;
import java.util.ArrayList;
import android.widget.ImageView;
import android.app.Activity;

import org.w3c.dom.Text;

/**
 * Created by samuel on 10/20/16.
 */
public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.DynamicViewHolder>  {

    private static ArrayList<String> currentList;
    private  SwipeDetector swipeDetector;

    public static Context context;

    public  Activity activity;

    private  RecyclerView mRecyclerView;

    public DynamicAdapter(ArrayList<String> items) {

        //this.activity = a;
        this.currentList = items;
        this.swipeDetector = new SwipeDetector();

    }

    @Override
    public int getItemCount() {
        return currentList.size();
    }

    @Override
    public void onBindViewHolder(DynamicViewHolder dynamicViewHolder, int i) {
        String currentItem = currentList.get(i);

        dynamicViewHolder.myCustomEditTextListener.updatePosition(dynamicViewHolder.getAdapterPosition());

        dynamicViewHolder.itemText.setText(currentItem);

        if(MainActivity.myCoupons.containsKey(currentItem)) {
            Bundle extras = (Bundle)MainActivity.myCoupons.get(currentItem);
            dynamicViewHolder.couponTextView.setText(extras.getString("title"));
            dynamicViewHolder.itemUrlTextView.setText(extras.getString("url"));

        }



    }
    @Override
    public DynamicViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row_layout, viewGroup, false);
        mRecyclerView = (RecyclerView) viewGroup;
        mRecyclerView.addOnItemTouchListener(swipeDetector);
        DynamicViewHolder dynamicViewHolder = new DynamicViewHolder(view, new MyCustomEditTextListener(), this);




        return dynamicViewHolder;
    }



    public class DynamicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        EditText itemText;
        Button deleteButton;
        Button editCouponButton;
        ImageView couponImageView;
        TextView couponTextView;
        TextView itemUrlTextView;
        public MyCustomEditTextListener myCustomEditTextListener;
        DynamicAdapter parentAdapter;


        public DynamicViewHolder(View v, MyCustomEditTextListener myCustomEditTextListener, DynamicAdapter dynamicAdapter) {
            super(v);

            this.itemText = (EditText) v.findViewById(R.id.itemText);
            this.deleteButton = (Button) v.findViewById(R.id.deleteButton);
            this.editCouponButton = (Button) v.findViewById(R.id.editCouponButton);
            this.couponImageView = (ImageView) v.findViewById(R.id.couponPic);
            this.couponTextView = (TextView) v.findViewById(R.id.couponDescription);
            this.itemUrlTextView = (TextView) v.findViewById(R.id.itemUrlTextView);
            this.myCustomEditTextListener = myCustomEditTextListener;
            itemText.addTextChangedListener(myCustomEditTextListener);
            this.myCustomEditTextListener.updatePosition(this.getAdapterPosition());
            this.parentAdapter = dynamicAdapter;


            deleteButton.setOnClickListener(this);

            editCouponButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), SearchActivity.class);
                    intent.putExtra("ItemName",currentList.get(getAdapterPosition()));
                    view.getContext().startActivity(intent);
                }
            });


        }

        @Override
        public void onClick(View v) {
            deleteItem(getAdapterPosition());

        }

        public void addItem(String newItem, int position) {
            currentList.add(position, newItem);
            parentAdapter.notifyItemRemoved(position);
        }

        public void deleteItem(int position) {
            MainActivity.myCoupons.remove(currentList.get(position));
            currentList.remove(position);
            parentAdapter.notifyItemRemoved(position);
            //MainActivity.myFragmentPagerAdapter.notifyDataSetChanged();
            this.myCustomEditTextListener.updatePosition(getAdapterPosition());
        }


    }

    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            String newItemName = charSequence.toString();
            System.out.println(position);
            if(position == currentList.size()) {
                position--;
            }
            currentList.set(position, newItemName);
            //MainActivity.myFragmentPagerAdapter.notifyDataSetChanged();
        }

        @Override
        public void afterTextChanged(Editable editable) {


        }
    }




}