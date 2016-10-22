package com.twofromkt.ecomap.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.db.GetPlaces;
import com.twofromkt.ecomap.db.Place;

import java.util.ArrayList;

import static com.twofromkt.ecomap.activities.CategoriesActivity.CHOSEN_KEY;
import static com.twofromkt.ecomap.activities.MapActivityUtil.addLocationSearch;
import static com.twofromkt.ecomap.activities.MapActivityUtil.addMarkers;
import static com.twofromkt.ecomap.activities.MapActivityUtil.clearMarkers;
import static com.twofromkt.ecomap.activities.MapActivityUtil.closeFloatingMenu;
import static com.twofromkt.ecomap.activities.MapActivityUtil.closeKeyboard;
import static com.twofromkt.ecomap.activities.MapActivityUtil.collapse;
import static com.twofromkt.ecomap.activities.MapActivityUtil.expand;
import static com.twofromkt.ecomap.activities.MapActivityUtil.showBottomInfo;
import static com.twofromkt.ecomap.activities.MapActivityUtil.showBottomList;
import static com.twofromkt.ecomap.util.LocationUtil.fromLatLngZoom;
import static com.twofromkt.ecomap.util.LocationUtil.getLocation;
import static com.twofromkt.ecomap.util.Util.*;

public class MapActivityAdapter implements OnMapReadyCallback,
        FloatingActionButton.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener, GoogleMap.OnMarkerClickListener,
        LoaderManager.LoaderCallbacks<ArrayList<? extends Place> > {

    private MapActivity act;

    public MapActivityAdapter(MapActivity activity) {
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
        closeKeyboard(act);
        act.nv.requestFocus();
    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {
        if (newState == DrawerLayout.STATE_DRAGGING && !act.drawerLayout.isDrawerOpen(act.nv)) {
            act.nv.requestFocus();
            closeKeyboard(act);
        }
    }

    @Override
    public void onClick(View v) {
        Location location = getLocation(act.locationManager, act.criteria);
        if (v == act.trashButton || v == act.cafeButton) {
            closeFloatingMenu(act);
        }
        if (v == act.trashButton) {
            Intent intent = new Intent(act.getApplicationContext(), CategoriesActivity.class);
            intent.putExtra(CHOSEN_KEY, act.chosen);
            act.startActivityForResult(intent, MapActivity.CHOOSE_TRASH_ACTIVITY);
            showBottomList(act);
        }
        if (v == act.locationButton) {
//            expand(act.checkboxes);
            if (ActivityCompat.checkSelfPermission(act, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            if (location != null) {
                moveMap(act.mMap, fromLatLngZoom(location.getLatitude(), location.getLongitude(), 10));
            }
        }
        if (v == act.cafeButton) {
            clearMarkers();
//            collapse(act.checkboxes);
            searchNearCafe();
        }
        if (v == act.menuButton) {
            act.drawerLayout.openDrawer(act.nv);
        }
        if (v == act.hideChecks)
            collapse(act.checkboxes, act.searchBox);
        if (v == act.showChecks) {
            expand(act.checkboxes, act.searchBox);
        }
    }

    private Bundle createBundle() {
        LatLng curr = act.mMap.getCameraPosition().target;
        Bundle bundle = new Bundle();
        bundle.putDouble(MapActivity.LAT, curr.latitude);
        bundle.putDouble(MapActivity.LNG, curr.longitude);
        bundle.putInt(GetPlaces.MODE, GetPlaces.NEAR);
        bundle.putFloat(GetPlaces.RADIUS, (float)1e15);
        return bundle;
    }

    void searchNearCafe() {
        Bundle b = createBundle();
        Loader<ArrayList<? extends Place> > l;
        b.putInt(GetPlaces.WHICH_PLACE, GetPlaces.CAFE);
        l = act.getSupportLoaderManager().restartLoader(
                MapActivity.LOADER, b, this);
        l.onContentChanged();
        showBottomList(act);
    }

    void searchNearTrashes() {
        Bundle bundle = createBundle();
        Loader<ArrayList<? extends Place> > l;
        bundle.putInt(GetPlaces.WHICH_PLACE, GetPlaces.TRASH);
        bundle.putBooleanArray(GetPlaces.CHOSEN, act.chosen);
        l = act.getSupportLoaderManager().restartLoader(
                MapActivity.LOADER, bundle, this);
        l.onContentChanged();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        showBottomInfo(act, true);
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
            moveMap(act.mMap, act.startPos);
        }
        if (ActivityCompat.checkSelfPermission(act, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(act,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MapActivity.GPS_REQUEST);
            return;
        }
        addLocationSearch(act, act.mMap);
    }

    @Override
    public Loader<ArrayList<? extends Place>> onCreateLoader(int id, Bundle args) {
        return new GetPlaces(act.getApplicationContext(), args);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<? extends Place>> loader, ArrayList<? extends Place> data) {
//        searchResults.clear();
//        searchResults.addAll(data);
//        act.searchAdapter.notifyItemRangeInserted(0, act.searchAdapter.getItemCount() - 1); //onDataSetChanged not working
        showBottomList(act, data);
    }


    @Override
    public void onLoaderReset(Loader<ArrayList<? extends Place>> loader) {

    }
}