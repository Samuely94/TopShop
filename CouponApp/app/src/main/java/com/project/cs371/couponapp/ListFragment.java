package com.project.cs371.couponapp;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



public class ListFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String ARG_LIST = "ARG_LIST";
    private ArrayList<String> currentList;
    private int mPage;
    protected LinearLayoutManager rv_layout_mgr;

    public static ListFragment newInstance(int page) {

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ArrayList<String> currentList = (ArrayList)MainActivity.myLists.get(page);
        args.putStringArrayList(ARG_LIST,currentList);
        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        MainActivity.myFragments.add(page, fragment);

        return fragment;
    }
    private boolean isViewShown = false;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPage = getArguments().getInt(ARG_PAGE);
        currentList = getArguments().getStringArrayList(ARG_LIST);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment_layout, container, false);
        System.out.println(mPage);
        RecyclerView myRecyclerView = (RecyclerView) view.findViewById(R.id.listFragment);
        rv_layout_mgr = new LinearLayoutManager(getActivity());
        ArrayList<String> currentList = (ArrayList)MainActivity.myLists.get(mPage);
        DynamicAdapter myDynamicAdapter = new DynamicAdapter(currentList);
        myDynamicAdapter.notifyDataSetChanged();
        myRecyclerView.setAdapter(myDynamicAdapter);
        myRecyclerView.setLayoutManager(rv_layout_mgr);
        return view;
    }
}