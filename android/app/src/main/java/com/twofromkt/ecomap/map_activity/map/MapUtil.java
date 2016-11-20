package com.twofromkt.ecomap.map_activity.map;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.Loader;
import android.util.Pair;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twofromkt.ecomap.db.GetPlaces;
import com.twofromkt.ecomap.db.Place;
import com.twofromkt.ecomap.map_activity.MapActivity;

import java.util.ArrayList;

import static com.twofromkt.ecomap.map_activity.MapActivity.CATEGORIES_N;
import static com.twofromkt.ecomap.util.LocationUtil.getLatLng;

class MapUtil {

    private MapView map;

    static volatile ArrayList<ArrayList<Pair<Marker, ? extends Place>>> activeMarkers;

    static {
        activeMarkers = new ArrayList<>();
        for (int i = 0; i < CATEGORIES_N; i++) {
            MapView.getActiveMarkers().add(new ArrayList<Pair<Marker, ? extends Place>>());
        }
    }

    MapUtil(MapView map) {
        this.map = map;
    }

    void searchNearCafe() {
        Bundle b = createBundle();
        Loader<Pair<CameraUpdate, ArrayList<? extends Place>>> loader;
        b.putInt(GetPlaces.WHICH_PLACE, GetPlaces.CAFE);
        loader = map.parentActivity.getSupportLoaderManager()
                .restartLoader(MapActivity.LOADER, b, map.parentActivity.adapter);
        loader.onContentChanged();
        map.parentActivity.bottomSheet.show();
    }

    void searchNearTrashes() {
        Bundle bundle = createBundle();
        Loader<Pair<CameraUpdate, ArrayList<? extends Place>>> loader;
        bundle.putInt(GetPlaces.WHICH_PLACE, GetPlaces.TRASH);
        bundle.putBooleanArray(GetPlaces.CHOSEN, map.parentActivity.bottomSheet.getTrashCategories());
        loader = map.parentActivity.getSupportLoaderManager()
                .restartLoader(MapActivity.LOADER, bundle, map.parentActivity.adapter);
        loader.onContentChanged();
    }

    void focusOnMarker(Pair<Marker, ? extends Place> a) {
        System.out.println("focus on marker");
        map.parentActivity.bottomSheet.hide();
        map.parentActivity.bottomInfo.show(true);
        map.parentActivity.bottomInfo.addInfo(a.second.name, a.second.getClass().getName());
//        moveMap(act.mMap, fromLatLngZoom(a.second.location.val1, a.second.location.val2, MAPZOOM));
    }

    Marker addMarker(Place x, int num) {
        Marker m = map.mMap.addMarker(new MarkerOptions().position(getLatLng(x.location)).title(x.name));
        activeMarkers.get(num).add(new Pair<>(m, x));
        return m;
    }

    <T extends Place> void addMarkers(ArrayList<T> p, CameraUpdate cu, int num) {
        clearMarkers(num);
        ArrayList<LatLng> pos = new ArrayList<>();
        for (Place place : p) {
            addMarker(place, num);
            pos.add(getLatLng(place.location));
        }
        if (pos.size() > 0) {
            map.mMap.animateCamera(cu);
        }
//        act.listPagerAdapter.notifyUpdate();

    }

    void clearMarkers(int num) {
        if (num == -1)
            return;
        for (Pair<Marker, ? extends Place> m : activeMarkers.get(num)) {
            m.first.remove();
        }
        activeMarkers.get(num).clear();
//        act.listPagerAdapter.notifyUpdate();
    }

    void addLocationSearch(MapActivity act) {
        if (ActivityCompat.checkSelfPermission(act,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.mMap.setMyLocationEnabled(true);
        map.mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    private Bundle createBundle() {
        LatLng curr = map.mMap.getCameraPosition().target;
        Bundle bundle = new Bundle();
        bundle.putDouble(MapActivity.LAT, curr.latitude);
        bundle.putDouble(MapActivity.LNG, curr.longitude);
        bundle.putInt(GetPlaces.MODE, GetPlaces.NEAR);
        bundle.putFloat(GetPlaces.RADIUS, (float) 1e15);
        return bundle;
    }

}
