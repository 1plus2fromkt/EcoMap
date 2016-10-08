package com.twofromkt.ecomap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twofromkt.ecomap.data_struct.Pair;
import com.twofromkt.ecomap.db.Filter;
import com.twofromkt.ecomap.db.TrashBox;
import com.twofromkt.ecomap.server.Downloader;

import java.util.ArrayList;
import java.util.Arrays;

import static com.twofromkt.ecomap.CategoriesActivity.CHOSEN_KEY;
import static com.twofromkt.ecomap.CategoriesActivity.TRASH_N;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        FloatingActionButton.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener{

    GoogleMap mMap;
    MapView mapView;
    FloatingActionButton cafeButton, trashButton, locationButton;
    CameraPosition startPos;
    FloatingActionMenu floatingMenu;
    SupportMapFragment mapFragment;
    EditText searchField;
    boolean[] chosen;
    NavigationView nv;
    Criteria criteria = new Criteria();
    LocationManager locationManager;
    DrawerLayout drawerLayout;

    static final String MENU_OPENED = "MENU_OPENED", LAT = "LAT", LNG = "LNG", ZOOM = "ZOOM",
            SEARCH_TEXT = "SEARCH_TEXT", NAV_BAR_OPENED = "NAV_BAR_OPENED", IS_EDIT_FOCUSED = "IS_EDIT_FOCUSED";
    static final int CHOOSE_TRASH_ACTIVITY = 0, GPS_REQUEST = 111;

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
        mapFragment.getMapAsync(this);
    }

    private void initFields() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        cafeButton = (FloatingActionButton) findViewById(R.id.cafe_button);
        searchField = (EditText) findViewById(R.id.search_edit);
        chosen = new boolean[TRASH_N];
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        trashButton = (FloatingActionButton) findViewById(R.id.trash_button);
        floatingMenu = (FloatingActionMenu) findViewById(R.id.menu);
        locationButton = (FloatingActionButton) findViewById(R.id.location_button);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        nv = (NavigationView) findViewById(R.id.nav_view);
    }

    private void setListeners() {
        trashButton.setOnClickListener(this);
        locationButton.setOnClickListener(this);
        nv.setNavigationItemSelectedListener(this);
        drawerLayout.addDrawerListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (startPos != null) {
            moveMap(mMap, startPos);
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    GPS_REQUEST);
            return;
        }
        addLocationSearch(mMap);
        displayTrashboxes();
    }

    private void displayTrashboxes() {
        ArrayList<Pair<Double, Double>> trashboxesCoords = Downloader.data;
        ArrayList<TrashBox> trashboxes = new ArrayList<>();
        for (int i = 0; i < trashboxesCoords.size(); i++) {
            trashboxes.add(new TrashBox(
                    new LatLng(trashboxesCoords.get(i).val1, trashboxesCoords.get(i).val2),
                    "place " + i,
                    null, null, TrashBox.Category.GLASS));
        }
        for (TrashBox trashBox : trashboxes) {
            if (chosen[2] || trashBox.information.equals("place 0")) { // kek lol
                addMarker(trashBox.location, trashBox.information);
            }
        }
    }

    private void addMarker(LatLng coord, String name) {
        mMap.addMarker(new MarkerOptions().position(coord).title(name));
    }

    private void addLocationSearch(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
    }
    
    @Override
    public void onClick(View v) {
        if (v == trashButton) {
            Intent intent = new Intent(getApplicationContext(), CategoriesActivity.class);
            intent.putExtra(CHOSEN_KEY, chosen);
            startActivityForResult(intent, CHOOSE_TRASH_ACTIVITY);
        }
        if (v == locationButton) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));
            if (location != null) {
                moveMap(mMap, fromLatLng(location.getLatitude(), location.getLongitude(), 10));
            }
        }
    }

    private CameraPosition fromLatLng(double a, double b, float z) {
        return CameraPosition.fromLatLngZoom(new LatLng(a, b), z);
    }

    private void moveMap(GoogleMap map, CameraPosition pos) {
        map.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
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

    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            try {
                startPos = fromLatLng(
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
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            } else {
                drawerLayout.requestFocus();
                closeKeyboard();
            }
            if (savedInstanceState.getBoolean(NAV_BAR_OPENED)) {
                drawerLayout.openDrawer(nv, true);
            }
            chosen = savedInstanceState.getBooleanArray(CHOSEN_KEY);
            searchField.setText(savedInstanceState.getCharSequence(SEARCH_TEXT));
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (CHOOSE_TRASH_ACTIVITY): {
                if (resultCode == Activity.RESULT_OK) {
                    chosen = data.getBooleanArrayExtra("CHOSEN_KEY");
                    displayTrashboxes();
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
        } else if (floatingMenu.isOpened()) {
            floatingMenu.close(true);
        } else {
            super.onBackPressed();
        }
        System.out.println("back pressed");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        closeKeyboard();
        nv.requestFocus();
    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {
        if (newState == DrawerLayout.STATE_DRAGGING && !drawerLayout.isDrawerOpen(nv)) {
            nv.requestFocus();
            closeKeyboard();
        }
    }

}
