package com.twofromkt.ecomap;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.twofromkt.ecomap.db.Cafe;
import com.twofromkt.ecomap.db.GetPlaces;
import com.twofromkt.ecomap.db.Place;

import java.util.ArrayList;

import static com.twofromkt.ecomap.CategoriesActivity.CHOSEN_KEY;
import static com.twofromkt.ecomap.Util.markersToPlace;

public class ListenerAdapter implements OnMapReadyCallback,
        FloatingActionButton.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener, GoogleMap.OnMarkerClickListener {

    private MapActivity act;

    public ListenerAdapter(MapActivity activity) {
        act = activity;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_settings) {
            Intent intent = new Intent(act.getApplicationContext(), SettingsActivity.class);
            act.startActivity(intent);
        }
        return false;
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        act.closeKeyboard();
        act.nv.requestFocus();
    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {
        if (newState == DrawerLayout.STATE_DRAGGING && !act.drawerLayout.isDrawerOpen(act.nv)) {
            act.nv.requestFocus();
            act.closeKeyboard();
        }
    }

    @Override
    public void onClick(View v) {
        Location location = act.locationManager.getLastKnownLocation(act.locationManager
                .getBestProvider(act.criteria, false));
        if (v == act.trashButton || v == act.cafeButton)
            act.closeFloatingMenu();
        if (v == act.trashButton) {

            Intent intent = new Intent(act.getApplicationContext(), CategoriesActivity.class);
            intent.putExtra(CHOSEN_KEY, act.chosen);
            act.startActivityForResult(intent, MapActivity.CHOOSE_TRASH_ACTIVITY);
            act.clearMarkers();
        }
        if (v == act.locationButton) {
            if (ActivityCompat.checkSelfPermission(act, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            if (location != null) {
                act.moveMap(act.mMap, act.fromLatLng(location.getLatitude(), location.getLongitude(), 10));
            }
        }
        if (v == act.cafeButton) {
            act.clearMarkers();
            ArrayList<Cafe> p = GetPlaces.getCafes(act.mMap.getCameraPosition().target, 1e12, act.getApplicationContext());
            act.addMarkers(p);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        act.showBottom();
        Place p = markersToPlace.get(marker);
        act.name.setText(p.name);
        act.category_name.setText(p.getClass().getName());
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        act.mMap = googleMap;
        act.mMap.setOnMarkerClickListener(this);
        if (act.startPos != null) {
            act.moveMap(act.mMap, act.startPos);
        }
        if (ActivityCompat.checkSelfPermission(act, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(act,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MapActivity.GPS_REQUEST);
            return;
        }
        act.addLocationSearch(act.mMap);
//        bottomInfo.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
}
