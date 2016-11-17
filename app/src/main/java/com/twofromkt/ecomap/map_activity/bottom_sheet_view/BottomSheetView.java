package com.twofromkt.ecomap.map_activity.bottom_sheet_view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.map_activity.MapActivity;

import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;

import static com.twofromkt.ecomap.util.Util.activeMarkers;

public class BottomSheetView extends RelativeLayout implements View.OnTouchListener {

    private ViewPagerBottomSheetBehavior bottomList;
    private ViewPager listViewPager, settViewPager;
    private TabLayout listTabLayout, settTabLayout;
    private ListViewPagerAdapter listPagerAdapter;
    private SettViewPagerAdapter settPagerAdapter;

    private RelativeLayout categoriesLayout;
    private RelativeLayout listLayout;
    private RelativeLayout collapsedPart;
    private FragmentManager fragmentManager;
    View bottomListView;

    boolean isCategory;

    private MapActivity parentActivity;

    public BottomSheetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.bottom_sheet, this);
    }

    public void attach(FragmentManager fragmentManager, MapActivity parentActivity) {
        this.parentActivity = parentActivity;
        this.fragmentManager = fragmentManager;

        initFields();
        setListeners();
//        bottomList.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void initFields() {
        bottomListView = findViewById(R.id.bottom_list);
        bottomList = ViewPagerBottomSheetBehavior.from(bottomListView);

        listPagerAdapter =
                new ListViewPagerAdapter(fragmentManager, activeMarkers, parentActivity);
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
        bottomList.setBottomSheetCallback(new ViewPagerBottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    listLayout.setVisibility(View.INVISIBLE);
                    categoriesLayout.setVisibility(View.INVISIBLE);
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED)
                    listViewPager.requestFocus();
            }

            @Override
            public void onSlide(@NonNull View view, float slideOffset) {
                if (slideOffset < 1) {
                    collapsedPart.setVisibility(View.VISIBLE);
                    collapsedPart.setAlpha(1 - slideOffset);
                    if (parentActivity.adapter.isCategory) {
                        categoriesLayout.setAlpha(slideOffset);
                        listLayout.setVisibility(View.INVISIBLE);
                    } else {
                        listLayout.setAlpha(slideOffset);
                        categoriesLayout.setVisibility(View.INVISIBLE);
                    }
                }
                if (slideOffset == 1) {
                    collapsedPart.setVisibility(View.INVISIBLE);
                }
            }
        });
        bottomListView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (bottomList.getState() == BottomSheetBehavior.STATE_COLLAPSED &&
                listLayout.getVisibility() == View.INVISIBLE &&
                categoriesLayout.getVisibility() == View.INVISIBLE) {
            isCategory = (event.getAxisValue(MotionEvent.AXIS_X) > v.getWidth() / 2);
            categoriesLayout.setAlpha(0);
            listLayout.setAlpha(0);
            listLayout.setVisibility(isCategory ? View.INVISIBLE : View.VISIBLE);
            categoriesLayout.setVisibility(isCategory ? View.VISIBLE : View.INVISIBLE);
            return true;
        }
        return false;
    }
}
