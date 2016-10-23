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
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.text.Line;
import com.twofromkt.ecomap.DividerItemDecorator;
import com.twofromkt.ecomap.Mock;
import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.db.Cafe;
import com.twofromkt.ecomap.db.TrashBox;

import java.util.HashSet;

import io.codetail.widget.RevealLinearLayout;

import static com.twofromkt.ecomap.activities.CategoriesActivity.CHOSEN_KEY;
import static com.twofromkt.ecomap.activities.CategoriesActivity.TRASH_N;
import static com.twofromkt.ecomap.activities.MapActivityUtil.addLocationSearch;
import static com.twofromkt.ecomap.activities.MapActivityUtil.addMarker;
import static com.twofromkt.ecomap.activities.MapActivityUtil.clearMarkers;
import static com.twofromkt.ecomap.activities.MapActivityUtil.closeFloatingMenu;
import static com.twofromkt.ecomap.activities.MapActivityUtil.closeKeyboard;
import static com.twofromkt.ecomap.activities.MapActivityUtil.hideBottomInfo;
import static com.twofromkt.ecomap.activities.MapActivityUtil.hideBottomList;
import static com.twofromkt.ecomap.activities.MapActivityUtil.isBottomOpened;
import static com.twofromkt.ecomap.activities.MapActivityUtil.showBottomInfo;
import static com.twofromkt.ecomap.util.LocationUtil.findNearestAddress;
import static com.twofromkt.ecomap.util.LocationUtil.fromLatLngZoom;
import static com.twofromkt.ecomap.util.LocationUtil.getLocation;
import static com.twofromkt.ecomap.util.Util.activeMarkers;
import static com.twofromkt.ecomap.util.Util.searchResults;

public class MapActivity extends FragmentActivity {

    GoogleMap mMap;
    BottomSheetBehavior bottomInfo, bottomList;
    View bottomInfoView, bottomListView;
    FloatingActionButton cafeButton, trashButton, locationButton, navigationButton;
    ImageButton cafeCheck, trashCheck, otherCheck;
    CameraPosition startPos;
    TextView name, category_name;
    FloatingActionMenu floatingMenu;
    SupportMapFragment mapFragment;
    ImageButton showChecks;
    EditText searchField;
    LinearLayout searchBox;
    boolean[] chosen;
    NavigationView nv;
    ListAdapter searchAdapter;
    Criteria criteria = new Criteria();
    LocationManager locationManager;
    DrawerLayout drawerLayout;
    LinearLayout checkboxes;
    MapActivityAdapter adapter;
    RecyclerView searchList;
    Button menuButton;

    final MapActivity thisActivity = this;

    static final String MENU_OPENED = "MENU_OPENED", LAT = "LAT", LNG = "LNG", ZOOM = "ZOOM",
            SEARCH_TEXT = "SEARCH_TEXT", NAV_BAR_OPENED = "NAV_BAR_OPENED",
            IS_EDIT_FOCUSED = "IS_EDIT_FOCUSED", NAME = "NAME", CATEGORY_NAME = "CATEGORY_NAME",
            BOTTOM_INFO_STATE = "BOTTOM_INFO_STATE", BOTTOM_LIST_STATE = "BOTTOM_LIST_STATE",
            CHECKBOXES_SHOWN = "CHECKBOXES_SHOWN", SEARCHBOX_SHOWN = "SEARCHBOX_SHOWN";
    static final int CHOOSE_TRASH_ACTIVITY = 0, GPS_REQUEST = 111, LOADER = 42;

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
        cafeButton = (FloatingActionButton) findViewById(R.id.cafe_button);
        searchField = (EditText) findViewById(R.id.search_edit);
        chosen = new boolean[TRASH_N];
        name = (TextView) findViewById(R.id.name_text);
        category_name = (TextView) findViewById(R.id.category_text);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        trashButton = (FloatingActionButton) findViewById(R.id.trash_button);
        floatingMenu = (FloatingActionMenu) findViewById(R.id.menu);
        checkboxes = (LinearLayout) findViewById(R.id.checkboxes);
        locationButton = (FloatingActionButton) findViewById(R.id.location_button);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        nv = (NavigationView) findViewById(R.id.nav_view);
        navigationButton = (FloatingActionButton) findViewById(R.id.nav_button);
        bottomInfoView = findViewById(R.id.bottom_sheet);
        bottomListView = findViewById(R.id.bottom_list);
        bottomInfo = BottomSheetBehavior.from(bottomInfoView);
        bottomList = BottomSheetBehavior.from(bottomListView);
        searchList = (RecyclerView) findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(this));
        searchList.addItemDecoration(new DividerItemDecorator(this));
        searchBox = (LinearLayout) findViewById(R.id.search_box);
        searchResults.add(new Cafe("Кафе 1", new LatLng(60.043175, 30.409615), "Мое первое кафе",
                null, "", "656-68-52", "", "www.vk.com"));
        searchAdapter = new ListAdapter(getApplicationContext(), searchResults);
        searchList.setAdapter(searchAdapter);
        menuButton = (Button) findViewById(R.id.menu_button);
        menuButton.setOnClickListener(adapter);
        showChecks = (ImageButton) findViewById(R.id.show_checkboxes);
        trashCheck = (ImageButton) findViewById(R.id.trash_checkbox);
        cafeCheck = (ImageButton) findViewById(R.id.cafe_checkbox);
        otherCheck = (ImageButton) findViewById(R.id.smth_checkbox);
    }

    private void setListeners() {
        trashCheck.setOnClickListener(adapter);
        cafeCheck.setOnClickListener(adapter);
        showChecks.setOnClickListener(adapter);
        locationButton.setOnClickListener(adapter);
        nv.setNavigationItemSelectedListener(adapter);
        drawerLayout.addDrawerListener(adapter);
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
                    addMarker(mMap, new TrashBox(
                            "found place",
                            new LatLng(address.getLatitude(), address.getLongitude()),
                            "info", null, "sosi", new HashSet<TrashBox.Category>()));
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
        state.putBoolean(MENU_OPENED, floatingMenu.isOpened());
        state.putBooleanArray(CHOSEN_KEY, chosen);
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
            if (savedInstanceState.getBoolean(MENU_OPENED)) {
                floatingMenu.open(false);
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
            chosen = savedInstanceState.getBooleanArray(CHOSEN_KEY);
            searchField.setText(savedInstanceState.getCharSequence(SEARCH_TEXT));
            bottomList.setState((int) savedInstanceState.get(BOTTOM_LIST_STATE));
            bottomInfo.setState((int) savedInstanceState.get(BOTTOM_INFO_STATE));
            searchBox.setVisibility((int) savedInstanceState.get(SEARCHBOX_SHOWN));
            checkboxes.setVisibility((int) savedInstanceState.get(CHECKBOXES_SHOWN));
            if (isBottomOpened(this)) {
                name.setText((String) savedInstanceState.get(NAME));
                category_name.setText((String) savedInstanceState.get(CATEGORY_NAME));
                showBottomInfo(this, false);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (CHOOSE_TRASH_ACTIVITY): {
                if (resultCode == Activity.RESULT_OK) {
                    clearMarkers();
                    chosen = data.getBooleanArrayExtra("CHOSEN_KEY");
                    adapter.searchNearTrashes();
                }
                break;
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(nv)) {
            drawerLayout.closeDrawer(nv);
        } else if (bottomInfo.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomInfo.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (bottomInfo.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            hideBottomInfo(this);
        } else if (floatingMenu.isOpened()) { // TODO: everything should be changed
            closeFloatingMenu(this);
        } else if (activeMarkers.size() > 0) {
            clearMarkers();
            hideBottomList(this);
        } else {
            super.onBackPressed();
        }
        System.out.println("back pressed");
    }
}