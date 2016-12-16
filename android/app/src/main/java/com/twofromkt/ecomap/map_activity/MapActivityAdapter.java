package com.twofromkt.ecomap.map_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.activities.SettingsActivity;
import com.twofromkt.ecomap.db.PlacesLoader;
import com.twofromkt.ecomap.db.PlaceResultType;

class MapActivityAdapter implements
        NavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener,
        LoaderManager.LoaderCallbacks<PlaceResultType> {

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

    // That loader is started from loadAllPlaces or loadPlace in MapUtil
    @Override
    public Loader<PlaceResultType> onCreateLoader(int id, Bundle args) {
        return new PlacesLoader(act.getApplicationContext(), args);
    }

    @Override
    public void onLoadFinished(Loader<PlaceResultType> loader,
                               PlaceResultType data) {
        act.searchBar.hideProgressBar();
        if (data.loadSuccess) {
            act.map.placesLoaded = true;
            int cat = data.number;
            if (data.searchById) {
                data.res.get(0).lite = false;
                act.bottomInfo.setPlace(data.res.get(0));
            } else {
                act.map.addMarkers(data.res, data.cameraUpdate, cat);
                // if cat was chosen, but places were not loaded at the moment, so
                // loadAllPlaces was called, and now it has finished:
                if (act.typePanel.isChosen(cat)) {
                    act.typePanel.setChosen(cat, true, true);
                }
            }
        } else {
            act.updateDatabase();
        }
    }

    @Override
    public void onLoaderReset(Loader<PlaceResultType> loader) {

    }

}