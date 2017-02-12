package com.project.cs371.couponapp;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;

import android.support.v4.app.*;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button addItemButton;
    Button createListButton;
    Button deleteTabButton;
    EditText newListEditText;
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static MyFragmentPagerAdapter myFragmentPagerAdapter;
    public volatile static ArrayList<String> listNames = new ArrayList<String>();;
    public volatile static List<List<String>> myLists = new ArrayList<List<String>>();
    public volatile static ArrayList<Fragment> myFragments = new ArrayList<Fragment>();
    public static HashMap myCoupons = new HashMap();
    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        addItemButton = (Button) findViewById(R.id.addItemButton);
        createListButton = (Button) findViewById(R.id.createListButton);
        deleteTabButton = (Button) findViewById(R.id.deleteTabButton);
        newListEditText = (EditText) findViewById(R.id.newListEditText);

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), getApplicationContext());
        viewPager = (ViewPager) findViewById(R.id.viewpager);
       //viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(),
                //MainActivity.this));
        viewPager.setAdapter(myFragmentPagerAdapter);

        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //myFragmentPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageSelected(int position) {

                viewPager.setCurrentItem(position);
                myFragmentPagerAdapter.notifyDataSetChanged();
                tabLayout.getTabAt(position).setCustomView(myFragmentPagerAdapter.getTabView(position));
                tabLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        createListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = newListEditText.getText().toString();
                System.out.println("Button Clicked");
                if(!title.equals("")) {
                    System.out.println("Title not empty");
                    addTab(title);
                    newListEditText.setText("");
                }
            }
        });
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myLists.size() > 0) {
                    myLists.get(viewPager.getCurrentItem()).add("New Item");
                    //myFragmentPagerAdapter.notifyDataSetChanged();
                    tabLayout.getTabAt(viewPager.getCurrentItem()).setCustomView(myFragmentPagerAdapter.getTabView(viewPager.getCurrentItem()));
                    tabLayout.setupWithViewPager(viewPager);
                }
                else {
                    Toast.makeText(getApplicationContext(), "No list to add item into!", Toast.LENGTH_LONG).show();
                }

            }
        });

        deleteTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewPager.getCurrentItem();
                myLists.remove(position);
                myFragments.remove(position);
                listNames.remove(position);
                myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), getApplicationContext());
                viewPager.setAdapter(myFragmentPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
                if(myLists.size() > 0) {
                    viewPager.setCurrentItem(position - 1);
                }
            }
        });

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Toast.makeText(getApplicationContext(), "Successfully linked with Facebook", Toast.LENGTH_LONG).show();
                //if(myLists.size() >= 0) {
                //viewPager.setCurrentItem(0);
                //}
            }
            @Override
            public void onCancel() {
                // App code
            }
            @Override
            public void onError(FacebookException exception) {
                // App code
            }

        });

    }





    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }



        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        public void removeTab(int position) {
            if (tabLayout.getTabCount() >= 1 && position < tabLayout.getTabCount()) {
                //tabLayout.removeTabAt(position);
                myFragmentPagerAdapter.removeTabPage(position);
            }
        }

        public void addTab(String title) {
            //tabLayout.addTab(tabLayout.newTab().setText(title));
            System.out.println("added tab");
            //myFragmentPagerAdapter.addTabPage(title);
            listNames.add(title);
            ArrayList<String> extra = new ArrayList<String>();
            extra.add("New Item");
            //myLists.add(new ArrayList<String>());
            myLists.add(extra);
            viewPager.setOffscreenPageLimit(0);
            myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), getApplicationContext());
            viewPager.setAdapter(myFragmentPagerAdapter);
            tabLayout.setupWithViewPager(viewPager);
            viewPager.setCurrentItem(myLists.size() - 1);
        }





}
