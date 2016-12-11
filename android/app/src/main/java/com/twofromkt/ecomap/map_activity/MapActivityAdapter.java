package com.twofromkt.ecomap.map_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.activities.SettingsActivity;
import com.twofromkt.ecomap.db.GetPlaces;
import com.twofromkt.ecomap.PlaceTypes.Place;
import com.twofromkt.ecomap.db.ResultType;

import java.util.ArrayList;

class MapActivityAdapter implements
        NavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener,
        LoaderManager.LoaderCallbacks<ResultType> {

    private MapActivity act;

    MapActivityAdapter(MapActivity activity) {
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
        MapActivityUtil.closeKeyboard(act);
//        act.nv.requestFocus();
    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {
//        if (newState == DrawerLayout.STATE_DRAGGING && !act.drawerLayout.isDrawerOpen(act.nv)) {
//            act.nv.requestFocus();
//            MapActivityUtil.closeKeyboard(act);
//        }
    }

    @Override
    public Loader<ResultType> onCreateLoader(int id, Bundle args) {
        return new GetPlaces(act.getApplicationContext(), args);
    }

    @Override
    public void onLoadFinished(Loader<ResultType> loader,
                               ResultType data) {
        Log.d("ADAPTER", "load finished");
        int t = data.number;
        act.map.addMarkers(data.res, data.cu, t, data.animate);
        act.map.onClusterUpdate();
//        act.bottomSheet.show(data.res, t);
    }

    @Override
    public void onLoaderReset(Loader<ResultType> loader) {

    }

}