package com.twofromkt.ecomap.map_activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Pair;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.twofromkt.ecomap.Mock;
import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.db.Place;
import com.twofromkt.ecomap.map_activity.bottom_info_view.BottomInfoView;
import com.twofromkt.ecomap.map_activity.bottom_sheet_view.BottomSheetView;
import com.twofromkt.ecomap.map_activity.map_view.MapView;
import com.twofromkt.ecomap.map_activity.search_bar_view.SearchBarView;

import java.util.ArrayList;

import static com.twofromkt.ecomap.settings.TrashSett.TRASH_N;
import static com.twofromkt.ecomap.util.Util.activeMarkers;

public class MapActivity extends FragmentActivity {

//    FloatingActionButton locationButton, navigationButton;

    TextView name, category_name;

    Button[] trashCategoryButtons;
//    NavigationView nv;
    DrawerLayout drawerLayout;

    public Criteria criteria = new Criteria();
    public LocationManager locationManager;
    public MapActivityAdapter adapter;
    public MapActivityUtil util;

    public SearchBarView searchBar;
    public MapView map;
    public BottomInfoView bottomInfo;
    public BottomSheetView bottomSheet;

    public static final String MENU_OPENED = "MENU_OPENED", LAT = "LAT", LNG = "LNG", ZOOM = "ZOOM",
            SEARCH_TEXT = "SEARCH_TEXT", NAV_BAR_OPENED = "NAV_BAR_OPENED",
            IS_EDIT_FOCUSED = "IS_EDIT_FOCUSED", NAME = "NAME", CATEGORY_NAME = "CATEGORY_NAME",
            BOTTOM_INFO_STATE = "BOTTOM_INFO_STATE", BOTTOM_LIST_STATE = "BOTTOM_LIST_STATE",
            CHECKBOXES_SHOWN = "CHECKBOXES_SHOWN", SEARCHBOX_SHOWN = "SEARCHBOX_SHOWN",
            CHECKBOXES_CHOSEN = "CHECKBOXES_CHOSEN", COLLAPSED_ALPHA = "COLLAPSED_ALPHA",
            LIST_ALPHA = "LIST_ALPHA", CATEGORIES_ALPHA = "CATEGORIES_ALPHA",
            CHOSEN_KEY = "CHOSEN_KEY";
    public static final int GPS_REQUEST = 111, LOADER = 42;
    public static final int CATEGORIES_N = 3, TRASH_NUM = 0, CAFE_NUM = 1, OTHER_NUM = 2;
    public static final float MAPZOOM = 14;

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
    }

    private void initFields() {
        map = (MapView) findViewById(R.id.map_view);
        map.attach(this, getSupportFragmentManager(), true);
        searchBar = (SearchBarView) findViewById(R.id.search_bar);
        searchBar.attach(this);
        bottomInfo = (BottomInfoView) findViewById(R.id.binfo);
        bottomInfo.attach(this);
        bottomSheet = (BottomSheetView) findViewById(R.id.bsheet);
        bottomSheet.attach(getSupportFragmentManager(), this);

        adapter = new MapActivityAdapter(this);
        util = new MapActivityUtil(this);

        trashCategoryButtons = new Button[TRASH_N];
        name = (TextView) findViewById(R.id.name_text);
        category_name = (TextView) findViewById(R.id.category_text);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        locationButton = (FloatingActionButton) findViewById(R.id.location_button);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        nv = (NavigationView) findViewById(R.id.nav_view);
//        navigationButton = (FloatingActionButton) findViewById(R.id.nav_button);

        if (activeMarkers.size() == 0)
            for (int i = 0; i < CATEGORIES_N; i++) { // TODO: replace this crap
                activeMarkers.add(new ArrayList<Pair<Marker, ? extends Place>>());
            }

    }

    private void setListeners() {
        MapActivityUtil.hideBottomInfo(this);
        MapActivityUtil.hideBottomList(this);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
//        state.putBooleanArray(CHOSEN_KEY, settPagerAdapter.trashSett.chosen);
//        if (mMap != null) { // mMap can be null if we turn phone just after onCreate
//            LatLng ll = mMap.getCameraPosition().target;
//            state.putDouble(LAT, ll.latitude);
//            state.putDouble(LNG, ll.longitude);
//            state.putFloat(ZOOM, mMap.getCameraPosition().zoom);
//        }
//        state.putCharSequence(SEARCH_TEXT, searchField.getText());
//        state.putBoolean(NAV_BAR_OPENED, drawerLayout.isDrawerOpen(nv));
//        state.putBoolean(IS_EDIT_FOCUSED, searchField.isFocused());
//        state.putInt(BOTTOM_LIST_STATE, bottomList.getState());
//        state.putInt(SEARCHBOX_SHOWN, searchBox.getVisibility());
//        state.putInt(BOTTOM_INFO_STATE, bottomInfo.getState());
//        state.putFloat(COLLAPSED_ALPHA, collapsedPart.getAlpha());
//        state.putFloat(CATEGORIES_ALPHA, categoriesLayout.getAlpha());
//        state.putFloat(LIST_ALPHA, listLayout.getAlpha());
//        state.putBooleanArray(CHECKBOXES_CHOSEN, chosenCheck);
//        state.putInt(CHECKBOXES_SHOWN, checkboxes.getVisibility());
//        if (MapActivityUtil.isBottomOpened(this)) {
//            state.putCharSequence(NAME, name.getText());
//            state.putCharSequence(CATEGORY_NAME, category_name.getText());
//        }

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        if (savedInstanceState != null) {
//            try {
//                startPos = fromLatLngZoom(
//                        (double) savedInstanceState.get(LAT),
//                        (double) savedInstanceState.get(LNG),
//                        (float) savedInstanceState.get(ZOOM));
//            } catch (Exception ignored) {
//
//            }
//            if (savedInstanceState.getBoolean(IS_EDIT_FOCUSED)) {
//                searchField.requestFocus();
//            } else {
//                drawerLayout.requestFocus();
//                MapActivityUtil.closeKeyboard(this);
//            }
//            if (savedInstanceState.getBoolean(NAV_BAR_OPENED)) {
//                drawerLayout.openDrawer(nv, true);
//            }
//            settPagerAdapter.trashSett.chosen = savedInstanceState.getBooleanArray(CHOSEN_KEY);
//            searchField.setText(savedInstanceState.getCharSequence(SEARCH_TEXT));
//            bottomList.setState((int) savedInstanceState.get(BOTTOM_LIST_STATE));
//            collapsedPart.setAlpha((float) savedInstanceState.get(COLLAPSED_ALPHA));
//            listLayout.setAlpha((float) savedInstanceState.get(LIST_ALPHA));
//            categoriesLayout.setAlpha((float) savedInstanceState.get(CATEGORIES_ALPHA));
//            bottomInfo.setState((int) savedInstanceState.get(BOTTOM_INFO_STATE));
//            chosenCheck = savedInstanceState.getBooleanArray(CHECKBOXES_CHOSEN);
//            searchBox.setVisibility((int) savedInstanceState.get(SEARCHBOX_SHOWN));
//            checkboxes.setVisibility((int) savedInstanceState.get(CHECKBOXES_SHOWN));
//            for (int i = 0; i < CATEGORIES_N; i++) {
//                if (!chosenCheck[i]) {
//                    checkboxButtons[i].setAlpha((float) 0.5);
//                } else {
//                    checkboxButtons[i].setAlpha((float) 1);
//                }
//            }
//            if (MapActivityUtil.isBottomOpened(this)) {
//                name.setText((String) savedInstanceState.get(NAME));
//                category_name.setText((String) savedInstanceState.get(CATEGORY_NAME));
//                MapActivityUtil.showBottomInfo(this, false);
//            }
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case GPS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
//                    MapActivityUtil.addLocationSearch(this, mMap);
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
//        if (drawerLayout.isDrawerOpen(nv)) {
//            drawerLayout.closeDrawer(nv);
//        } else if (bottomInfo.getState() == BottomSheetBehavior.STATE_EXPANDED) {
//            bottomInfo.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        } else if (bottomInfo.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
//            MapActivityUtil.hideBottomInfo(this);
//        } else if (checkMarkers()) {
//            util.clearMarkers(TRASH_NUM);
//            util.clearMarkers(CAFE_NUM);
//            MapActivityUtil.hideBottomList(this);
//        } else {
//            super.onBackPressed();
//        }
    }

    public void searchTrashes() {
        map.searchNearTrashes();
    }

    public void searchCafe() {
        map.searchNearCafe();
    }

}