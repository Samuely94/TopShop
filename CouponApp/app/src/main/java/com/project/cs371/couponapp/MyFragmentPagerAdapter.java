package com.project.cs371.couponapp;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

import static com.project.cs371.couponapp.MainActivity.listNames;
import static com.project.cs371.couponapp.MainActivity.myFragments;


/**
 * Created by Samuel on 11/9/2016.
 */

public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
    public int PAGE_COUNT;

    private Context context;



    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        PAGE_COUNT = listNames.size();

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        //System.out.println("getItem " + position);
        ListFragment.newInstance(position);
        return myFragments.get(position);
        //return ListFragment.newInstance(position);

    }


    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return listNames.get(position);
    }

    @Override
    public int getItemPosition(Object Obj) {
        return POSITION_NONE;
    }



    public void removeTabPage(int position) {
        if (!listNames.isEmpty() && position < listNames.size()) {
            listNames.remove(position);
            //myLists.remove(position);
            notifyDataSetChanged();
        }
    }

    public void addTabPage(String title) {
        listNames.add(title);

        System.out.println("updated adapter");
        notifyDataSetChanged();
    }

    public View getTabView(final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_fragment_layout, null);
        RecyclerView myRecyclerView = (RecyclerView) view.findViewById(R.id.listFragment);
        LinearLayoutManager rv_layout_mgr = new LinearLayoutManager(context);
        ArrayList<String> currentList = (ArrayList)MainActivity.myLists.get(position);
        DynamicAdapter myDynamicAdapter = new DynamicAdapter(currentList);
        myRecyclerView.setAdapter(myDynamicAdapter);
        myRecyclerView.setLayoutManager(rv_layout_mgr);
        System.out.println("Current View =  " + position);

        return view;
    }
}


