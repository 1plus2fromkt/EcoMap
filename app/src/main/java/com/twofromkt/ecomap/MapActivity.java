package com.twofromkt.ecomap;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, FloatingActionButton.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    MapView mapView;
    private FloatingActionButton cafeButton, trashButton;
    private CameraPosition startPos;
    private FloatingActionMenu floatingMenu;
    SupportMapFragment mapFragment;
    Fragment fragment;
    boolean[] chosen;
    NavigationView nv;

    static final String MENU_OPENED = "MENU_OPENED", LAT = "LAT", LNG = "LNG", ZOOM = "ZOOM";
    static final int CHOOSE_TRASH_ACTIVITY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        cafeButton = (FloatingActionButton) findViewById(R.id.cafe_button);
        trashButton = (FloatingActionButton) findViewById(R.id.trash_button);
        floatingMenu = (FloatingActionMenu) findViewById(R.id.menu);
        fragment = getFragmentManager().findFragmentById(R.id.map);
        if (savedInstanceState != null) {
            startPos = CameraPosition.fromLatLngZoom(new LatLng(
                    (double) savedInstanceState.get(LAT),
                    (double) savedInstanceState.get(LNG)),
                    (float) savedInstanceState.get(ZOOM));
            if (savedInstanceState.getBoolean(MENU_OPENED)) {
                floatingMenu.open(false);
            }
        }
        mapFragment.getMapAsync(this);
        trashButton.setOnClickListener(this);
        nv = (NavigationView) findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (startPos != null)
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(startPos));
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onClick(View v) {
        if (v == trashButton) {
            Intent intent = new Intent(getApplicationContext(), CategoriesActivity.class);
            startActivityForResult(intent, CHOOSE_TRASH_ACTIVITY);
        }
    }

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean(MENU_OPENED, floatingMenu.isOpened());
        LatLng ll = mMap.getCameraPosition().target;
        state.putDouble(LAT, ll.latitude);
        state.putDouble(LNG, ll.longitude);
        state.putFloat(ZOOM, mMap.getCameraPosition().zoom);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            startPos = CameraPosition.fromLatLngZoom(new LatLng(
                            (double) savedInstanceState.get(LAT),
                            (double) savedInstanceState.get(LNG)),
                    (float) savedInstanceState.get(ZOOM));
            if (savedInstanceState.getBoolean(MENU_OPENED)) {
                floatingMenu.open(false);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (CHOOSE_TRASH_ACTIVITY) : {
                if (resultCode == Activity.RESULT_OK) {
                    chosen = data.getBooleanArrayExtra("CHOSEN_KEY");
                }
                break;
            }
        }
        System.out.println(Arrays.toString(chosen));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        }
        return false;
    }
}
