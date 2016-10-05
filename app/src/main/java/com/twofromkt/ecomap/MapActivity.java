package com.twofromkt.ecomap;

import android.app.Fragment;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, FloatingActionButton.OnClickListener {

    private GoogleMap mMap;
    MapView mapView;
    private FloatingActionButton cafeButton, trashButton;
    private CameraPosition startPos;
    private FloatingActionMenu menuButton;
    SupportMapFragment mapFragment;
    Fragment fragment;
    String menu_open = "MENU_OPEN", lat = "LAT", lng = "LNG", zoom = "ZOOM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        cafeButton = (FloatingActionButton) findViewById(R.id.cafe_button);
        trashButton = (FloatingActionButton) findViewById(R.id.trash_button);
        menuButton = (FloatingActionMenu) findViewById(R.id.menu);
        fragment = getFragmentManager().findFragmentById(R.id.map);
        if (savedInstanceState != null) {
            startPos = CameraPosition.fromLatLngZoom(
                    new LatLng((double) savedInstanceState.get(lat), (double) savedInstanceState.get(lng)),
                    (float) savedInstanceState.get(zoom));
            if (!(boolean)savedInstanceState.get(menu_open)) {
                menuButton.open(true);
            }
        }
        mapFragment.getMapAsync(this);
        trashButton.setOnClickListener(this);

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
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                    startPos));
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    @Override
    public void onClick(View v) {
        if (v == trashButton) {
            Intent intent = new Intent(getApplicationContext(), Categories.class);
            startActivity(intent);
        }
    }

    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean(menu_open, menuButton.isMenuHidden());
        LatLng ll = mMap.getCameraPosition().target;
        state.putDouble(lat, ll.latitude);
        state.putDouble(lng, ll.longitude);
        state.putFloat(zoom, mMap.getCameraPosition().zoom);
    }
}
