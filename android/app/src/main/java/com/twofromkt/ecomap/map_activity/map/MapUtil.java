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
import com.twofromkt.ecomap.PlaceTypes.Place;
import com.twofromkt.ecomap.db.ResultType;
import com.twofromkt.ecomap.map_activity.MapActivity;

import java.util.ArrayList;

import static com.twofromkt.ecomap.Consts.CAFE_NUMBER;
import static com.twofromkt.ecomap.Consts.CATEGORIES_N;
import static com.twofromkt.ecomap.Consts.TRASH_NUMBER;
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
        Loader<ResultType> loader;
        b.putInt(GetPlaces.WHICH_PLACE, CAFE_NUMBER);
        loader = map.parentActivity.getSupportLoaderManager()
                .restartLoader(MapActivity.LOADER, b, map.parentActivity.adapter);
        loader.onContentChanged();
        map.parentActivity.bottomSheet.collapse();
    }

    void searchNearTrashes() {
        Bundle bundle = createBundle();
        Loader<ResultType> loader;
        bundle.putInt(GetPlaces.WHICH_PLACE, TRASH_NUMBER);
        bundle.putInt(GetPlaces.ANY_MATCH_KEY, GetPlaces.ONE_MATCH);
        bundle.putBooleanArray(GetPlaces.CHOSEN, map.parentActivity.bottomSheet.getTrashCategories());
        // TODO: also, when bounds are big, like whole country, we shouldn't show anything. Like Google does. Or we should show cities.
        loader = map.parentActivity.getSupportLoaderManager()
                .restartLoader(MapActivity.LOADER, bundle, map.parentActivity.adapter);
        loader.onContentChanged();
        map.parentActivity.bottomSheet.collapse();
    }

    void focusOnMarker(Pair<Marker, ? extends Place> a) {
        System.out.println("focus on marker");
        map.parentActivity.bottomSheet.hide();
        map.parentActivity.bottomInfo.collapse();
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
        map.parentActivity.bottomSheet.notifyChange();

    }

    void clearMarkers(int num) {
        if (num == -1)
            return;
        for (Pair<Marker, ? extends Place> m : activeMarkers.get(num)) {
            m.first.remove();
        }
        activeMarkers.get(num).clear();
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

    private Bundle createBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(GetPlaces.MODE, GetPlaces.IN_BOUNDS);
        LatLng x = map.mMap.getProjection().getVisibleRegion().latLngBounds.northeast;
        LatLng y = map.mMap.getProjection().getVisibleRegion().latLngBounds.southwest;
        bundle.putDouble(GetPlaces.LAT_MINUS, y.latitude);
        bundle.putDouble(GetPlaces.LNG_MINUS, y.longitude);
        bundle.putDouble(GetPlaces.LAT_PLUS, x.latitude);
        bundle.putDouble(GetPlaces.LNG_PLUS, x.longitude);
        return bundle;
    }

}
