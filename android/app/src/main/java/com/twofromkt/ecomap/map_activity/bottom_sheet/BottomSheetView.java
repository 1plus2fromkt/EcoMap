package com.twofromkt.ecomap.map_activity.bottom_sheet;

import android.content.Context;
import android.os.Parcelable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.db.Place;
import com.twofromkt.ecomap.map_activity.MapActivity;
import com.twofromkt.ecomap.map_activity.map.MapView;

import java.util.ArrayList;

import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;

public class BottomSheetView extends RelativeLayout {

    ViewPagerBottomSheetBehavior bottomList;
    ViewPager listViewPager, settViewPager;
    TabLayout listTabLayout, settTabLayout;
    ListViewPagerAdapter listPagerAdapter;
    SettViewPagerAdapter settPagerAdapter;

    RelativeLayout categoriesLayout;
    RelativeLayout listLayout;
    RelativeLayout collapsedPart;
    FragmentManager fragmentManager;
    View bottomListView;

    MapActivity parentActivity;

    private BottomSheetAdapter adapter;

    public BottomSheetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.bottom_sheet, this);
    }

    public void attach(FragmentManager fragmentManager, MapActivity parentActivity) {
        this.parentActivity = parentActivity;
        this.fragmentManager = fragmentManager;

        adapter = new BottomSheetAdapter(this);
        initFields();
        setListeners();
    }

    private void initFields() {
        bottomListView = findViewById(R.id.bottom_list);
        bottomList = ViewPagerBottomSheetBehavior.from(bottomListView);

        listPagerAdapter =
                new ListViewPagerAdapter(fragmentManager, MapView.getActiveMarkers(), parentActivity);
        settPagerAdapter = new SettViewPagerAdapter(fragmentManager, parentActivity);

        listViewPager = (ViewPager) findViewById(R.id.list_viewpager);
        settViewPager = (ViewPager) findViewById(R.id.sett_viewpager);
        listViewPager.setAdapter(listPagerAdapter);
        settViewPager.setAdapter(settPagerAdapter);
        listTabLayout = (TabLayout) findViewById(R.id.list_tabs);
        listTabLayout.setupWithViewPager(listViewPager);
        settTabLayout = (TabLayout) findViewById(R.id.sett_tabs);
        settTabLayout.setupWithViewPager(settViewPager);

        categoriesLayout = (RelativeLayout) findViewById(R.id.categories_layout);
        collapsedPart = (RelativeLayout) findViewById(R.id.collapsed_part);
        listLayout = (RelativeLayout) findViewById(R.id.list_layout);

    }

    private void setListeners() {
        bottomList.setBottomSheetCallback(adapter);
        bottomListView.setOnTouchListener(adapter);
    }

    public void focusOnTab(int i) {
        listViewPager.setCurrentItem(i);
        settViewPager.setCurrentItem(i);
    }

    public boolean[] getTrashCategories() {
        return settPagerAdapter.trashSett.chosen;
    }

    public void show() {
//        act.locationButton.setVisibility(View.INVISIBLE);
        bottomList.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void show(ArrayList<? extends Place> data, int num) {
        if (bottomList.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            show();
        }
    }

    public void hide() {
//        act.locationButton.setVisibility(View.VISIBLE);
        bottomList.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superSaved = super.onSaveInstanceState();
        return new SavedBottomSheet(superSaved, bottomList.getState());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedBottomSheet savedState = (SavedBottomSheet) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        bottomList.setState(savedState.getState());
    }
}
