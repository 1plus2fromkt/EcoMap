package com.twofromkt.ecomap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twofromkt.ecomap.data_struct.Pair;
import com.twofromkt.ecomap.db.Cafe;
import com.twofromkt.ecomap.db.GetPlaces;
import com.twofromkt.ecomap.db.Place;
import com.twofromkt.ecomap.db.TrashBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static com.twofromkt.ecomap.CategoriesActivity.CHOSEN_KEY;
import static com.twofromkt.ecomap.CategoriesActivity.TRASH_N;
import static com.twofromkt.ecomap.db.GetPlaces.*;
import static com.twofromkt.ecomap.Util.*;
import static com.twofromkt.ecomap.db.TrashBox.Category.AND;
import static com.twofromkt.ecomap.db.TrashBox.Category.GLASS;

public class MapActivity extends FragmentActivity {

    public static class MyEditText extends EditText{

        public MyEditText(Context context) {
            super(context);
        }

        public MyEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public boolean onKeyPreIme(int keyCode, KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                System.out.println("HUI");
            }
            return super.dispatchKeyEvent(event);
        }
    }

    GoogleMap mMap;
    BottomSheetBehavior bottomInfo;
    View bottomInfoView;
    FloatingActionButton cafeButton, trashButton, locationButton, navigationButton;
    CameraPosition startPos;
    TextView name, category_name;
    FloatingActionMenu floatingMenu;
    SupportMapFragment mapFragment;
    EditText searchField;
    boolean[] chosen;
    NavigationView nv;
    Criteria criteria = new Criteria();
    LocationManager locationManager;
    DrawerLayout drawerLayout;

    ListenerAdapter adapter;

    static final String MENU_OPENED = "MENU_OPENED", LAT = "LAT", LNG = "LNG", ZOOM = "ZOOM",
            SEARCH_TEXT = "SEARCH_TEXT", NAV_BAR_OPENED = "NAV_BAR_OPENED", IS_EDIT_FOCUSED = "IS_EDIT_FOCUSED",
            NAME = "NAME", CATEGORY_NAME = "CATEGORY_NAME", BOTTOM_STATE = "BOTTOM_STATE";
    static final int CHOOSE_TRASH_ACTIVITY = 0, GPS_REQUEST = 111;

    @Override
    protected void onStart() {
        super.onStart();
        putObjects();
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
        adapter = new ListenerAdapter(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        cafeButton = (FloatingActionButton) findViewById(R.id.cafe_button);
        searchField = (EditText) findViewById(R.id.search_edit);
        chosen = new boolean[TRASH_N];
        name = (TextView) findViewById(R.id.name_text);
        category_name = (TextView) findViewById(R.id.category_text);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        trashButton = (FloatingActionButton) findViewById(R.id.trash_button);
        floatingMenu = (FloatingActionMenu) findViewById(R.id.menu);
        locationButton = (FloatingActionButton) findViewById(R.id.location_button);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        nv = (NavigationView) findViewById(R.id.nav_view);
        navigationButton = (FloatingActionButton) findViewById(R.id.nav_button);
        bottomInfoView = findViewById(R.id.bottom_sheet);
        bottomInfo = BottomSheetBehavior.from(bottomInfoView);
    }

    private void setListeners() {
        trashButton.setOnClickListener(adapter);
        cafeButton.setOnClickListener(adapter);
        locationButton.setOnClickListener(adapter);
        nv.setNavigationItemSelectedListener(adapter);
        drawerLayout.addDrawerListener(adapter);
        bottomInfo.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN)
                    hideBottom();
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        hideBottom();
    }

    void putObjects() {
        GetPlaces.putObject(new Cafe("Кафе 1", new LatLng(60.043175, 30.409615), "Мое первое кафе",
                null, "", "656-68-52", "", "www.vk.com"), 0, getApplicationContext());
        GetPlaces.putObject(new Cafe("Кафе 2", new LatLng(60.143175, 30.509615), "Мое второе кафе",
                null, "", "656-68-53", "", "www.vk.ru"), 0, getApplicationContext());
        HashSet<TrashBox.Category> h = new HashSet<>();
        h.add(GLASS);
        h.add(AND);
        GetPlaces.putObject(new TrashBox("Урна 1", new LatLng(60.193175, 30.359615), "Моя первая урна",
                null, "", h), 1, getApplicationContext());
        h.remove(AND);
        GetPlaces.putObject(new TrashBox("Урна 2", new LatLng(60.163175, 30.359615), "Моя вторая урна",
                null, "", h), 1, getApplicationContext());

    }

    protected void addMarker(Place x) {
        Marker m = mMap.addMarker(new MarkerOptions().position(fromPair(x.location)).title(x.name));
        currMarkers.add(m);
        markersToPlace.put(m, x);
    }

    protected void addLocationSearch(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
    }

    protected void clearMarkers() {
        for (Marker m : currMarkers)
            m.remove();
        currMarkers = new ArrayList<>();
        markersToPlace = new HashMap<>();
    }

    protected  <T extends Place> void addMarkers(ArrayList<T> p) {
        ArrayList<LatLng> pos = new ArrayList<>();
        for (Place place : p) {
            addMarker(place);
            pos.add(fromPair(place.location));
        }
        Pair<LatLng, Double> cent = center(pos);
//        moveMap(mMap, fromLatLngZoom(cent.val1, calculateZoomLevel(mMap.)));
    }

    protected CameraPosition fromLatLngZoom(double a, double b, float z) {
        return CameraPosition.fromLatLngZoom(new LatLng(a, b), z);
    }
    protected CameraPosition fromLatLngZoom(LatLng x, float z) {
        return fromLatLngZoom(x.latitude, x.longitude, z);
    }

    protected void moveMap(GoogleMap map, CameraPosition pos) {
        map.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    private int calculateZoomLevel(int screenWidth, int radii) {
        double equatorLength = 40075004; // in meters
        double widthInPixels = screenWidth;
        double metersPerPixel = equatorLength / 256;
        int zoomLevel = 1;
        while ((metersPerPixel * widthInPixels) > 2000) {
            metersPerPixel /= 2;
            ++zoomLevel;
        }
        return zoomLevel;
    }

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
        state.putInt(BOTTOM_STATE, bottomInfo.getState());
        if (isBottomOpened()) {
            state.putCharSequence(NAME, name.getText());
            state.putCharSequence(CATEGORY_NAME, category_name.getText());
        }

    }

    protected boolean isBottomOpened() {
        return bottomInfo.getState() != BottomSheetBehavior.STATE_HIDDEN;
    }

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
//                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            } else {
                drawerLayout.requestFocus();
                closeKeyboard();
            }
            if (savedInstanceState.getBoolean(NAV_BAR_OPENED)) {
                drawerLayout.openDrawer(nv, true);
            }
            chosen = savedInstanceState.getBooleanArray(CHOSEN_KEY);
            searchField.setText(savedInstanceState.getCharSequence(SEARCH_TEXT));
            bottomInfo.setState((int) savedInstanceState.get(BOTTOM_STATE));
            if (isBottomOpened()) {
                name.setText((String)savedInstanceState.get(NAME));
                category_name.setText((String)savedInstanceState.get(CATEGORY_NAME));
                showBottom(false);
            }
        }
    }

    protected void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void closeFloatingMenu() {
        floatingMenu.close(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (CHOOSE_TRASH_ACTIVITY): {
                if (resultCode == Activity.RESULT_OK) {
                    chosen = data.getBooleanArrayExtra("CHOSEN_KEY");
                    Location location = locationManager.getLastKnownLocation(locationManager
                            .getBestProvider(criteria, false));
                    ArrayList<TrashBox> t = getTrashes(new LatLng(location.getLatitude(),
                            location.getLongitude()), 1e12, chosen, getApplicationContext());
                    clearMarkers();
                    addMarkers(t);
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
                    addLocationSearch(mMap);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(nv)) {
            drawerLayout.closeDrawer(nv);
        } else if (bottomInfo.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomInfo.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (bottomInfo.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            hideBottom();
        } else if (floatingMenu.isOpened()) {
            closeFloatingMenu();
        } else if (currMarkers.size() > 0) {
            clearMarkers();
        } else {
            super.onBackPressed();
        }
        System.out.println("back pressed");
    }

    protected void showBottom (boolean showSheet) {
        navigationButton.setVisibility(View.VISIBLE);
        locationButton.setVisibility(View.INVISIBLE);
        floatingMenu.setVisibility(View.INVISIBLE);
        if (showSheet)
            bottomInfo.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    protected void hideBottom() {
        navigationButton.setVisibility(View.INVISIBLE);
        locationButton.setVisibility(View.VISIBLE);
        floatingMenu.setVisibility(View.VISIBLE);
        bottomInfo.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}