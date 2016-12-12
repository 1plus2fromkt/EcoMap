package com.twofromkt.ecomap.map_activity.map;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.Loader;
import android.util.Pair;

import com.android.internal.util.Predicate;
import com.google.android.gms.maps.CameraUpdate;
import com.twofromkt.ecomap.PlaceTypes.Place;
import com.twofromkt.ecomap.PlaceTypes.TrashBox;
import com.twofromkt.ecomap.db.GetPlaces;
import com.twofromkt.ecomap.db.ResultType;
import com.twofromkt.ecomap.map_activity.MapActivity;

import java.util.ArrayList;

import static com.twofromkt.ecomap.Consts.CAFE_ID;
import static com.twofromkt.ecomap.Consts.CATEGORIES_NUMBER;
import static com.twofromkt.ecomap.Consts.TRASH_ID;
import static com.twofromkt.ecomap.db.GetPlaces.LITE;
import static com.twofromkt.ecomap.util.LocationUtil.getLatLng;

class MapUtil {

    private MapView map;

    static volatile ArrayList<ArrayList<Pair<MapClusterItem, ? extends Place>>> activeMarkers;

    static {
        activeMarkers = new ArrayList<>();
        for (int i = 0; i < CATEGORIES_NUMBER; i++) {
            MapView.getActiveMarkers().add(new ArrayList<Pair<MapClusterItem, ? extends Place>>());
        }
    }

    MapUtil(MapView map) {
        this.map = map;
    }

    private void showPlaces(final int category, final Predicate<Place> predicate) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Pair<MapClusterItem, ? extends Place> x : activeMarkers.get(category)) {
                    if (predicate.apply(x.second)) {
                        showMarker(x.first);
                    }
                }
            }
        }).start();
    }

    void showCafeMarkers() {
        showPlaces(CAFE_ID, new Predicate<Place>() {
            @Override
            public boolean apply(Place place) {
                return true;
            }
        });
    }

    void showTrashMarkers(final boolean anyMatch) {
        showPlaces(TRASH_ID, new Predicate<Place>() {
            @Override
            public boolean apply(Place place) {
                TrashBox t = (TrashBox) place;
                boolean any = false, all = true;
                for (int i = 0; i < CATEGORIES_NUMBER; i++) {
                    if (map.parentActivity.bottomSheet.isChecked(i) && t.isOfCategory(i)) {
                        any = true;
                    } else {
                        all = false;
                    }
                }
                if (anyMatch)
                    return any;
                else
                    return all;
            }
        });
    }

    void focusOnMarker(Pair<MapClusterItem, ? extends Place> a) {
        map.parentActivity.bottomSheet.hide();
        map.parentActivity.bottomInfo.collapse();
        map.parentActivity.bottomInfo.addInfo(a.second.getName(), a.second.getClass().getName());
//        moveMap(act.mMap, fromLatLngZoom(a.second.location.val1, a.second.location.val2, MAPZOOM));
    }

    private void showMarker(MapClusterItem item) {
        map.clusterManager.addItem(item);
    }

    void addMarker(Place place, int type) {
        MapClusterItem clusterItem = new MapClusterItem(place);
        activeMarkers.get(type).add(new Pair<>(clusterItem, place));
    }

    <T extends Place> void addMarkers(ArrayList<T> p, CameraUpdate cu, int num) {
        clearMarkers(num);
        for (Place place : p) {
            addMarker(place, num);
        }

    }

    void clearMarkers(int num) {
        if (num == -1) {
            return;
        }
        for (Pair<MapClusterItem, ? extends Place> m : activeMarkers.get(num)) {
            map.clusterManager.removeItem(m.first);
        }
//        activeMarkers.get(num).clear();
        map.parentActivity.bottomSheet.notifyChange();
    }

    void addLocationSearch(MapActivity act) {
        if (ActivityCompat.checkSelfPermission(act,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.mMap.setMyLocationEnabled(true);
        map.mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    void loadAllPlaces() {
        for (int i = 0; i < 1; i++) {
            activeMarkers.get(i).clear();
            Bundle bundle = new Bundle();
            Loader<ResultType> loader;
            bundle.putInt(GetPlaces.WHICH_PLACE, i);
            bundle.putBoolean(LITE, true);
            bundle.putInt(GetPlaces.MODE, GetPlaces.ALL);
            loader = map.parentActivity.getSupportLoaderManager()
                    .restartLoader(MapActivity.LOADER, bundle, map.parentActivity.adapter);
            loader.onContentChanged();
        }
    }
}
