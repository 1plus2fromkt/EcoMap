package com.twofromkt.ecomap.map_activity.map;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.Loader;
import android.util.Pair;

import com.android.internal.util.Predicate;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.LatLng;
import com.twofromkt.ecomap.place_types.Place;
import com.twofromkt.ecomap.place_types.TrashBox;
import com.twofromkt.ecomap.db.PlacesLoader;
import com.twofromkt.ecomap.db.PlaceResultType;
import com.twofromkt.ecomap.map_activity.MapActivity;
import com.twofromkt.ecomap.util.LocationUtil;

import java.util.ArrayList;
import java.util.List;

import static com.twofromkt.ecomap.Consts.CAFE_ID;
import static com.twofromkt.ecomap.Consts.CATEGORIES_NUMBER;
import static com.twofromkt.ecomap.Consts.TRASH_ID;
import static com.twofromkt.ecomap.Consts.TRASH_TYPES_NUMBER;
import static com.twofromkt.ecomap.db.PlacesLoader.BY_ID;
import static com.twofromkt.ecomap.db.PlacesLoader.ID;
import static com.twofromkt.ecomap.db.PlacesLoader.LITE;
import static com.twofromkt.ecomap.db.PlacesLoader.MODE;
import static com.twofromkt.ecomap.map_activity.MapActivity.LOADER;
import static com.twofromkt.ecomap.util.LocationUtil.getLatLng;

public class MapUtil {

    private MapView map;
    private PlaceShower shower;
    private Thread showerThread;

    public static volatile ArrayList<ArrayList<Pair<MapClusterItem, ? extends Place>>> allMarkers,
            shownMarkers;

    static {
        allMarkers = new ArrayList<>();
        shownMarkers = new ArrayList<>();
        for (int i = 0; i < CATEGORIES_NUMBER; i++) {
            allMarkers.add(new ArrayList<Pair<MapClusterItem, ? extends Place>>());
            shownMarkers.add(new ArrayList<Pair<MapClusterItem, ? extends Place>>());
        }
    }

    MapUtil(MapView map) {
        this.map = map;
    }

    private class PlaceShower implements Runnable {
        boolean stop;
        private int category;
        private Predicate<Place> predicate;

        PlaceShower(int category, Predicate<Place> predicate) {
            this.category = category;
            this.predicate = predicate;
        }

        @Override
        public void run() {
            for (Pair<MapClusterItem, ? extends Place> m : shownMarkers.get(category)) {
                try {
                    map.clusterManager.removeItem(m.first);
                } catch (Exception ignored) {
                }
                if (stop) {
                    return;
                }
            }
            shownMarkers.get(category).clear();
            for (Pair<MapClusterItem, ? extends Place> x : allMarkers.get(category)) {
                if (predicate.apply(x.second)) {
                    showMarker(x, category);
                }
                if (stop) {
                    return;
                }
            }
            map.parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    map.clusterManager.cluster();
                    map.parentActivity.bottomSheet.notifyChange();
                }
            });

        }
    }

    private void showPlaces(final int category, final Predicate<Place> predicate) {
        //TODO replace that
        if (shower != null && showerThread.isAlive()) {
            shower.stop = true;
        }
        shower = new PlaceShower(category, predicate);
        showerThread = new Thread(shower);
        showerThread.start();
    }

    void showCafeMarkers() {
        showPlaces(CAFE_ID, new Predicate<Place>() {
            @Override
            public boolean apply(Place place) {
                return true;
            }
        });
    }

    /**
     * Show trash markers which are inside (or near) the visible rectangle.
     *
     * @param anyMatch show places that match chosen types exactly or just in one of types
     */
    void showTrashMarkers(final boolean anyMatch) {
        final LatLng min = map.getMap().getProjection().getVisibleRegion().latLngBounds.southwest,
                max = map.getMap().getProjection().getVisibleRegion().latLngBounds.northeast;
        final double ZOOM_THRESHOLD = 12.6;
        final int outOfBounds = map.getMap().getCameraPosition().zoom > ZOOM_THRESHOLD ? 1 : 0;
        showPlaces(TRASH_ID, new Predicate<Place>() {
            @Override
            public boolean apply(Place place) {
                TrashBox t = (TrashBox) place;
                boolean any = false, all = true, inBounds = inBounds(min, max,
                        getLatLng(place.getLocation()), outOfBounds);
                for (int i = 0; i < TRASH_TYPES_NUMBER; i++) {
                    if (map.parentActivity.bottomSheet.isChecked(i) && t.isOfCategory(i)) {
                        any = true;
                    } else {
                        all = false;
                    }
                }
                if (anyMatch) {
                    return any && inBounds;
                } else {
                    return all && inBounds;
                }
            }
        });
    }

    private boolean inBounds(LatLng min, LatLng max, LatLng x, int outOfBounds) {
        double dLat = (max.latitude - min.latitude) / 4 * outOfBounds,
                dLng = (max.longitude - min.longitude) / 2 * outOfBounds;
        return min.latitude - dLat <= x.latitude &&
                x.latitude <= max.latitude + dLat &&
                min.longitude - dLng <= x.longitude &&
                x.longitude <= max.longitude + dLng;
    }

    void focusOnMarker(Pair<MapClusterItem, ? extends Place> value) {
        map.parentActivity.bottomSheet.hide();
        map.parentActivity.bottomInfo.collapse();
        Place place = value.second;
        if (place.lite) {
            map.loadPlace(place.getId(), place.getCategoryNumber());
        } else {
            map.parentActivity.bottomInfo.setPlace(place);
        }
        map.moveMap(LocationUtil.fromLatLngZoom(LocationUtil.getLatLng(place.getLocation()),
                MapView.MAPZOOM));
    }

    private void showMarker(Pair<MapClusterItem, ? extends Place> p, int category) {
        map.clusterManager.addItem(p.first);
        shownMarkers.get(category).add(p);
    }

    /**
     * Add place to the list without showing it
     *
     * @param place
     * @param type
     */
    void addMarker(Place place, int type) {
        MapClusterItem clusterItem = new MapClusterItem(place);
        allMarkers.get(type).add(new Pair<>(clusterItem, place));
    }

    /**
     * Add all markers from the list, not showing them
     *
     * @param p
     * @param cu
     * @param num
     * @param <T>
     */
    <T extends Place> void addMarkers(List<T> p, CameraUpdate cu, int num) {
        clearMarkers(num, true);
        for (Place place : p) {
            addMarker(place, num);
        }
        map.parentActivity.bottomSheet.notifyChange();
    }

    /**
     * Remove all markers of a specific category from list and from cluster manager
     *
     * @param cat
     * @param toCluster
     */
    void clearMarkers(int cat, final boolean toCluster) {
        if (cat == -1) {
            return;
        }
        for (Pair<MapClusterItem, ? extends Place> m : shownMarkers.get(cat)) {
            try {
                map.clusterManager.removeItem(m.first);
            } catch (Exception ignored) {
            }
        }
        shownMarkers.get(cat).clear();
        if (map.clusterManager != null) {
            map.parentActivity.runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     if (toCluster) {
                         map.clusterManager.cluster();
                     }
                     map.parentActivity.bottomSheet.notifyChange();
                 }
            });
        }
    }

    // Is called when user accepts all permissions we need
    void addLocationSearch(MapActivity act) {
        if (ActivityCompat.checkSelfPermission(act,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //TODO move the code we need to another method because now it will not be inited if used denies location
        map.adapter.onMapReady(map.mMap);
    }

    void loadAllPlaces() {
        map.parentActivity.searchBar.showProgressBar();
        for (int i = 0; i < 1; i++) {
            allMarkers.get(i).clear();
            Bundle bundle = new Bundle();
            Loader<PlaceResultType> loader;
            bundle.putInt(PlacesLoader.WHICH_PLACE, i);
            bundle.putBoolean(LITE, true);
            bundle.putInt(MODE, PlacesLoader.ALL);
            //TODO should check if loader is already running
            loader = map.parentActivity.getSupportLoaderManager()
                    .restartLoader(LOADER, bundle, map.parentActivity.adapter);
            loader.onContentChanged();
        }
    }

    void loadPlace(int id, int category) {
        Bundle bundle = new Bundle();
        bundle.putInt(PlacesLoader.WHICH_PLACE, category);
        bundle.putBoolean(LITE, false);
        bundle.putInt(MODE, BY_ID);
        bundle.putInt(ID, id);
        map.parentActivity.getSupportLoaderManager().restartLoader(LOADER, bundle,
                map.parentActivity.adapter).onContentChanged();
    }
}
