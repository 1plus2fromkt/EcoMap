package com.twofromkt.ecomap.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.twofromkt.ecomap.Mock;
import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.db.Place;
import com.twofromkt.ecomap.db.TrashBox;

import java.util.ArrayList;
import java.util.HashSet;

import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;

import static com.twofromkt.ecomap.activities.MapActivityUtil.addLocationSearch;
import static com.twofromkt.ecomap.activities.MapActivityUtil.closeKeyboard;
import static com.twofromkt.ecomap.activities.MapActivityUtil.hideBottomInfo;
import static com.twofromkt.ecomap.activities.MapActivityUtil.hideBottomList;
import static com.twofromkt.ecomap.activities.MapActivityUtil.isBottomOpened;
import static com.twofromkt.ecomap.activities.MapActivityUtil.showBottomInfo;
import static com.twofromkt.ecomap.settings.TrashSett.TRASH_N;
import static com.twofromkt.ecomap.util.LocationUtil.findNearestAddress;
import static com.twofromkt.ecomap.util.LocationUtil.fromLatLngZoom;
import static com.twofromkt.ecomap.util.LocationUtil.getLocation;
import static com.twofromkt.ecomap.util.Util.activeMarkers;

public class MapActivity extends FragmentActivity {

    GoogleMap mMap;
    BottomSheetBehavior bottomInfo;
    ViewPagerBottomSheetBehavior bottomList;
    View bottomInfoView, bottomListView;
    FloatingActionButton locationButton, navigationButton;
    ImageButton[] checkboxButtons;
    CameraPosition startPos;
    TextView name, category_name;
    SupportMapFragment mapFragment;
    ImageButton showChecks;
    OneList[] lists;
    RelativeLayout categoriesLayout;
    RelativeLayout listLayout;
    RelativeLayout settListLayout;
    RelativeLayout collapsedPart;
    EditText searchField;
    LinearLayout searchBox;
    ListViewPagerAdapter listPagerAdapter;
    SettViewPagerAdapter settPagerAdapter;
    public boolean[] chosenCheck;
    Button[] trashCategoryButtons;
    NavigationView nv;
    Criteria criteria = new Criteria();
    LocationManager locationManager;
    DrawerLayout drawerLayout;
    LinearLayout checkboxes;
    MapActivityAdapter adapter;
    Button menuButton;
    ViewPager listViewPager, settViewPager;
    TabLayout listTabLayout, settTabLayout;
    MapActivityUtil util;

    final MapActivity thisActivity = this;

    static final String MENU_OPENED = "MENU_OPENED", LAT = "LAT", LNG = "LNG", ZOOM = "ZOOM",
            SEARCH_TEXT = "SEARCH_TEXT", NAV_BAR_OPENED = "NAV_BAR_OPENED",
            IS_EDIT_FOCUSED = "IS_EDIT_FOCUSED", NAME = "NAME", CATEGORY_NAME = "CATEGORY_NAME",
            BOTTOM_INFO_STATE = "BOTTOM_INFO_STATE", BOTTOM_LIST_STATE = "BOTTOM_LIST_STATE",
            CHECKBOXES_SHOWN = "CHECKBOXES_SHOWN", SEARCHBOX_SHOWN = "SEARCHBOX_SHOWN",
            CHECKBOXES_CHOSEN = "CHECKBOXES_CHOSEN", COLLAPSED_ALPHA = "COLLAPSED_ALPHA",
            LIST_ALPHA = "LIST_ALPHA", CATEGORIES_ALPHA = "CATEGORIES_ALPHA",
            CHOSEN_KEY = "CHOSEN_KEY";
    static final int GPS_REQUEST = 111, LOADER = 42;
    public static final int CATEGORIES_N = 3, TRASH_NUM = 0, CAFE_NUM = 1, OTHER_NUM = 2;
    public static final float MAPZOOM = 14  ;

    @Override
    protected void onStart() {
        super.onStart();

        Mock.putObjects(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initFields();
        setListeners();

        searchField.setCursorVisible(false);
        searchField.setHint("Search query");
        if (savedInstanceState == null) {
            mapFragment.setRetainInstance(true);
        }
        mapFragment.getMapAsync(adapter);
    }

    private void initFields() {
        adapter = new MapActivityAdapter(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        searchField = (EditText) findViewById(R.id.search_edit);
        trashCategoryButtons = new Button[TRASH_N];
        name = (TextView) findViewById(R.id.name_text);
        category_name = (TextView) findViewById(R.id.category_text);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        categoriesLayout = (RelativeLayout) findViewById(R.id.categories_layout);
        settListLayout = (RelativeLayout) findViewById(R.id.sett_list_layout);
        collapsedPart = (RelativeLayout) findViewById(R.id.collapsed_part);
        listLayout = (RelativeLayout) findViewById(R.id.list_layout);
        checkboxes = (LinearLayout) findViewById(R.id.checkboxes);
        locationButton = (FloatingActionButton) findViewById(R.id.location_button);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        nv = (NavigationView) findViewById(R.id.nav_view);
        navigationButton = (FloatingActionButton) findViewById(R.id.nav_button);
        bottomInfoView = findViewById(R.id.bottom_sheet);
        bottomListView = findViewById(R.id.bottom_list);
        bottomInfo = BottomSheetBehavior.from(bottomInfoView);
        bottomList = ViewPagerBottomSheetBehavior.from(bottomListView);
        searchBox = (LinearLayout) findViewById(R.id.search_box);
        if (activeMarkers.size() == 0)
            for (int i = 0; i < CATEGORIES_N; i++) { // TODO: replace this crap
                activeMarkers.add(new ArrayList<Pair<Marker, ? extends Place>>());
            }
        menuButton = (Button) findViewById(R.id.menu_button);
        menuButton.setOnClickListener(adapter);
        showChecks = (ImageButton) findViewById(R.id.show_checkboxes);
        checkboxButtons = new ImageButton[]{(ImageButton) findViewById(R.id.trash_checkbox),
                (ImageButton) findViewById(R.id.cafe_checkbox),
                (ImageButton) findViewById(R.id.smth_checkbox)};
        chosenCheck = new boolean[CATEGORIES_N];

        listViewPager = (ViewPager) findViewById(R.id.list_viewpager);
        settViewPager = (ViewPager) findViewById(R.id.sett_viewpager);
        listPagerAdapter =
                new ListViewPagerAdapter(getSupportFragmentManager(), activeMarkers, this);
        settPagerAdapter = new SettViewPagerAdapter(getSupportFragmentManager(), this);
        listViewPager.setAdapter(listPagerAdapter);
        settViewPager.setAdapter(settPagerAdapter);
        listTabLayout = (TabLayout) findViewById(R.id.list_tabs);
        listTabLayout.setupWithViewPager(listViewPager);
        settTabLayout = (TabLayout) findViewById(R.id.sett_tabs);
        settTabLayout.setupWithViewPager(settViewPager);
        util = new MapActivityUtil(this);
    }

    private void setListeners() {
        for (ImageButton i : checkboxButtons)
            i.setOnClickListener(adapter);
        showChecks.setOnClickListener(adapter);
        locationButton.setOnClickListener(adapter);
        nv.setNavigationItemSelectedListener(adapter);
        bottomListView.setOnTouchListener(adapter);
        drawerLayout.addDrawerListener(adapter);
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
//                    Log.d("Log", "onSlideBottom");
                    collapsedPart.setVisibility(View.VISIBLE);
                    collapsedPart.setAlpha(1 - slideOffset);
                    if (adapter.isCategory) {
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
        bottomInfo.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN)
                    hideBottomInfo(thisActivity);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Address address = findNearestAddress(searchField.getText().toString(),
                            thisActivity, getLocation(locationManager, criteria));
                    util.addMarker(mMap, new TrashBox(
                            "found place",
                            new LatLng(address.getLatitude(), address.getLongitude()),
                            "info", null, "sosi", new HashSet<TrashBox.Category>()), TRASH_NUM);
                    closeKeyboard(thisActivity);
                }
                return true;
            }
        });
        hideBottomInfo(this);
        hideBottomList(this);

    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBooleanArray(CHOSEN_KEY, settPagerAdapter.trashSett.chosen);
        if (mMap != null) { // mMap can be null if we turn phone just after onCreate
            LatLng ll = mMap.getCameraPosition().target;
            state.putDouble(LAT, ll.latitude);
            state.putDouble(LNG, ll.longitude);
            state.putFloat(ZOOM, mMap.getCameraPosition().zoom);
        }
        state.putCharSequence(SEARCH_TEXT, searchField.getText());
        state.putBoolean(NAV_BAR_OPENED, drawerLayout.isDrawerOpen(nv));
        state.putBoolean(IS_EDIT_FOCUSED, searchField.isFocused());
        state.putInt(BOTTOM_LIST_STATE, bottomList.getState());
        state.putInt(SEARCHBOX_SHOWN, searchBox.getVisibility());
        state.putInt(BOTTOM_INFO_STATE, bottomInfo.getState());
        state.putFloat(COLLAPSED_ALPHA, collapsedPart.getAlpha());
        state.putFloat(CATEGORIES_ALPHA, categoriesLayout.getAlpha());
        state.putFloat(LIST_ALPHA, listLayout.getAlpha());
        state.putBooleanArray(CHECKBOXES_CHOSEN, chosenCheck);
        state.putInt(CHECKBOXES_SHOWN, checkboxes.getVisibility());
        if (isBottomOpened(this)) {
            state.putCharSequence(NAME, name.getText());
            state.putCharSequence(CATEGORY_NAME, category_name.getText());
        }

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            try {
                startPos = fromLatLngZoom(
                        (double) savedInstanceState.get(LAT),
                        (double) savedInstanceState.get(LNG),
                        (float) savedInstanceState.get(ZOOM));
            } catch (Exception ignored) {

            }
            if (savedInstanceState.getBoolean(IS_EDIT_FOCUSED)) {
                searchField.requestFocus();
            } else {
                drawerLayout.requestFocus();
                closeKeyboard(this);
            }
            if (savedInstanceState.getBoolean(NAV_BAR_OPENED)) {
                drawerLayout.openDrawer(nv, true);
            }
            settPagerAdapter.trashSett.chosen = savedInstanceState.getBooleanArray(CHOSEN_KEY);
            searchField.setText(savedInstanceState.getCharSequence(SEARCH_TEXT));
            bottomList.setState((int) savedInstanceState.get(BOTTOM_LIST_STATE));
            collapsedPart.setAlpha((float) savedInstanceState.get(COLLAPSED_ALPHA));
            listLayout.setAlpha((float) savedInstanceState.get(LIST_ALPHA));
            categoriesLayout.setAlpha((float) savedInstanceState.get(CATEGORIES_ALPHA));
            bottomInfo.setState((int) savedInstanceState.get(BOTTOM_INFO_STATE));
            chosenCheck = savedInstanceState.getBooleanArray(CHECKBOXES_CHOSEN);
            searchBox.setVisibility((int) savedInstanceState.get(SEARCHBOX_SHOWN));
            checkboxes.setVisibility((int) savedInstanceState.get(CHECKBOXES_SHOWN));
            for (int i = 0; i < CATEGORIES_N; i++) {
                if (!chosenCheck[i])
                    checkboxButtons[i].setAlpha((float) 0.5);
                else
                    checkboxButtons[i].setAlpha((float) 1);
            }
            if (isBottomOpened(this)) {
                name.setText((String) savedInstanceState.get(NAME));
                category_name.setText((String) savedInstanceState.get(CATEGORY_NAME));
                showBottomInfo(this, false);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case GPS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    addLocationSearch(this, mMap);
        }
    }

    private boolean checkMarkers() {
        for (int i = 0; i < CATEGORIES_N; i++)
            if (activeMarkers.get(i).size() > 0)
                return true;
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(nv)) {
            drawerLayout.closeDrawer(nv);
        } else if (bottomInfo.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomInfo.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (bottomInfo.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            hideBottomInfo(this);
        } else if (checkMarkers()) {
            util.clearMarkers(TRASH_NUM);
            util.clearMarkers(CAFE_NUM);
            hideBottomList(this);
        } else {
            super.onBackPressed();
        }
        System.out.println("back pressed");
    }

    public void searchTrashes() {
        adapter.searchNearTrashes();
    }

    public void searchCafe() {
        adapter.searchNearCafe();
    }


}