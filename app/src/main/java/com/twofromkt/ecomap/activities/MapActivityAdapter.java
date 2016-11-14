package com.twofromkt.ecomap.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.util.Pair;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.db.GetPlaces;
import com.twofromkt.ecomap.db.Place;

import java.util.ArrayList;

import static com.twofromkt.ecomap.activities.MapActivity.CAFE_NUM;
import static com.twofromkt.ecomap.activities.MapActivity.CATEGORIES_N;
import static com.twofromkt.ecomap.activities.MapActivity.MAPZOOM;
import static com.twofromkt.ecomap.activities.MapActivity.TRASH_NUM;
import static com.twofromkt.ecomap.activities.MapActivity.ZOOM;
import static com.twofromkt.ecomap.activities.MapActivityUtil.addLocationSearch;
import static com.twofromkt.ecomap.activities.MapActivityUtil.closeKeyboard;
import static com.twofromkt.ecomap.activities.MapActivityUtil.collapse;
import static com.twofromkt.ecomap.activities.MapActivityUtil.expand;
import static com.twofromkt.ecomap.activities.MapActivityUtil.hideBottomList;
import static com.twofromkt.ecomap.activities.MapActivityUtil.showBottomInfo;
import static com.twofromkt.ecomap.activities.MapActivityUtil.showBottomList;
import static com.twofromkt.ecomap.settings.TrashSett.TRASH_N;
import static com.twofromkt.ecomap.util.LocationUtil.fromLatLngZoom;
import static com.twofromkt.ecomap.util.LocationUtil.getLocation;
import static com.twofromkt.ecomap.util.Util.*;

public class MapActivityAdapter implements OnMapReadyCallback,
        FloatingActionButton.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener, GoogleMap.OnMarkerClickListener,
        LoaderManager.LoaderCallbacks<Pair<CameraUpdate, ArrayList<? extends Place> > >, View.OnTouchListener{

    private MapActivity act;
    boolean isCategory = false;

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
        for (int i = 0; i < CATEGORIES_N; i++) {
            if (v == act.checkboxButtons[i]) {
                if (act.chosenCheck[i]) {
                    act.chosenCheck[i] = false;
                    act.checkboxButtons[i].setAlpha((float) 0.5);
                    act.util.clearMarkers(i);


                } else {
                    act.chosenCheck[i] = true;
                    act.checkboxButtons[i].setAlpha((float) 1);
                    if (i == TRASH_NUM)
                        searchNearTrashes();
                    else if (i == CAFE_NUM)
                        searchNearCafe();
                    act.listViewPager.setCurrentItem(i, true);
                }
                return;
            }
        }
        if (v == act.locationButton) {
            if (ActivityCompat.checkSelfPermission(act, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            if (location != null) {
                moveMap(act.mMap, fromLatLngZoom(location.getLatitude(), location.getLongitude(), MAPZOOM));
            }
            return;
        }
        if (v == act.menuButton) {
            act.drawerLayout.openDrawer(act.nv);
            return;
        }
        if (v == act.showChecks) {
            v.setRotation(act.showChecks.getRotation() + 180);
            if (act.checkboxes.getVisibility() == View.VISIBLE)
                collapse(act.checkboxes, act.searchBox);
            else
                expand(act.checkboxes, act.searchBox);
            return;
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
        Loader<Pair<CameraUpdate, ArrayList<? extends Place> > > l;
        b.putInt(GetPlaces.WHICH_PLACE, GetPlaces.CAFE);
        l = act.getSupportLoaderManager().restartLoader(
                MapActivity.LOADER, b, this);
        l.onContentChanged();
        showBottomList(act);
    }

    void searchNearTrashes() {
        Bundle bundle = createBundle();
        Loader<Pair<CameraUpdate, ArrayList<? extends Place> > > l;
        bundle.putInt(GetPlaces.WHICH_PLACE, GetPlaces.TRASH);
        bundle.putBooleanArray(GetPlaces.CHOSEN, act.settPagerAdapter.trashSett.chosen);
        l = act.getSupportLoaderManager().restartLoader(
                MapActivity.LOADER, bundle, this);
        l.onContentChanged();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        hideBottomList(act);
        showBottomInfo(act, true);
        Place p = null;
        for (ArrayList<Pair<Marker, ? extends Place> > ac : activeMarkers) {
            for (Pair<Marker, ? extends Place> x : ac)
                if (x.first.equals(marker)) {
                    p = x.second;
                    break;
                }
        }
        act.name.setText(p.name);
        act.category_name.setText(p.getClass().getName());
        return true;
    }

    public void focusOnMarker(Pair<Marker, ? extends Place> a) {
        hideBottomList(act);
        showBottomInfo(act, true);
        act.name.setText(a.second.name);
        act.category_name.setText(a.second.getClass().getName());
        moveMap(act.mMap, fromLatLngZoom(a.second.location.val1, a.second.location.val2, MAPZOOM));
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
    public Loader<Pair<CameraUpdate, ArrayList<? extends Place> > > onCreateLoader(int id, Bundle args) {
        return new GetPlaces(act.getApplicationContext(), args);
    }

    @Override
    public void onLoadFinished(Loader<Pair<CameraUpdate, ArrayList<? extends Place> > > loader,
                               Pair<CameraUpdate, ArrayList<? extends Place> > data) {
        int t = data.second.size() > 0 ? data.second.get(0).category_number : -1;
        act.util.addMarkers(data.second, data.first, act.mMap, t);
        showBottomList(act, data.second, t);
    }


    @Override
    public void onLoaderReset(Loader<Pair<CameraUpdate, ArrayList<? extends Place> > > loader) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == act.bottomListView &&
                act.bottomList.getState() == BottomSheetBehavior.STATE_COLLAPSED &&
                act.listLayout.getVisibility() == View.INVISIBLE &&
                act.categoriesLayout.getVisibility() == View.INVISIBLE) {
            isCategory = (event.getAxisValue(MotionEvent.AXIS_X) > v.getWidth() / 2);
            act.categoriesLayout.setAlpha(0);
            act.listLayout.setAlpha(0);
            act.listLayout.setVisibility(isCategory ? View.INVISIBLE : View.VISIBLE);
            act.categoriesLayout.setVisibility(isCategory ? View.VISIBLE : View.INVISIBLE);
            return true;
        }
        return false;
    }


}
