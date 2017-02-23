package com.twofromkt.ecomap.map_activity.map;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.Loader;

import com.android.internal.util.Predicate;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.LatLng;
import com.twofromkt.ecomap.db.PlaceResultType;
import com.twofromkt.ecomap.db.PlacesLoader;
import com.twofromkt.ecomap.map_activity.MapActivity;
import com.twofromkt.ecomap.map_activity.search_bar.SearchBarView;
import com.twofromkt.ecomap.place_types.Place;
import com.twofromkt.ecomap.place_types.TrashBox;
import com.twofromkt.ecomap.util.LocationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import static com.twofromkt.ecomap.util.Util.PlaceWithCoord;

public class MapUtil {

    private MapView map;
    private PlaceShower shower;
    private Thread showerThread;

    MapUtil(MapView map) {
        this.map = map;
    }

    private class PlaceShower implements Runnable {
        private boolean stop = false;
        private int category;
        private Predicate<Place> predicate;

        PlaceShower(int category, Predicate<Place> predicate) {
            this.category = category;
            this.predicate = predicate;
        }

        void stop() {
            stop = true;
        }

        @Override
        public void run() {
            // we should not use iterators to prevent concurrent modifications
            for (int i = 0; i < PlacesHolder.getInstance().getShown(category).size(); i++) {
                PlaceWithCoord m = PlacesHolder.getInstance().getShown(category).get(i);
                try {
                    map.clusterManager.removeItem(m.coordinates);
                } catch (Exception ignored) {
                }
                if (stop) {
                    return;
                }
            }
            PlacesHolder.getInstance().getShown(category).clear();
            for (int i = 0; i < PlacesHolder.getInstance().getAll(category).size(); i++) {
                PlaceWithCoord x = PlacesHolder.getInstance().getAll(category).get(i);
                if (predicate.apply(x.place)) {
                    showMarker(x, category);
                }
                if (stop) {
                    return;
                }
            }
            Location currLocation = map.getLocation();
            final LatLng currCoords = LocationUtil.getLatLng(
                    currLocation.getLatitude(), currLocation.getLongitude());
            Collections.sort(PlacesHolder.getInstance().getShown(category), new Comparator<PlaceWithCoord>() {
                @Override
                public int compare(PlaceWithCoord o1, PlaceWithCoord o2) {
                    // TODO understand why this happens
                    if (o1 == null || o2 == null) {
                        if (o1 == null && o2 == null) {
                            return 0;
                        } else if (o1 == null) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                    int dist1 = (int) LocationUtil.distanceLatLng(currCoords,
                            LocationUtil.getLatLng(o1.place.getLocation()));
                    int dist2 = (int) LocationUtil.distanceLatLng(currCoords,
                                    LocationUtil.getLatLng(o2.place.getLocation()));
                    return Integer.compare(dist1, dist2);
                }
            });
            map.parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    map.clusterManager.cluster();
                    map.parentActivity.bottomSheet
                            .updateList(category, PlacesHolder.getInstance().getShown(category));
                }
            });

        }
    }

    private void showPlaces(final int category, final Predicate<Place> predicate) {
        //TODO replace that
        if (shower != null && showerThread.isAlive()) {
            shower.stop();
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

    void focusOnMarker(PlaceWithCoord value) {
        map.parentActivity.bottomSheet.hide();
        map.parentActivity.bottomInfo.collapse();
        Place place = value.place;
        if (place.lite) {
            map.loadPlace(place.getId(), place.getCategoryNumber());
        } else {
            map.parentActivity.bottomInfo.setPlace(place);
        }
        map.moveMap(LocationUtil.fromLatLngZoom(LocationUtil.getLatLng(place.getLocation()),
                MapView.MAPZOOM));
    }

    private void showMarker(PlaceWithCoord p, int type) {
        map.clusterManager.addItem(p.coordinates);
        PlacesHolder.getInstance().getShown(type).add(p);
    }

    /**
     * Add place to the list without showing it
     *
     * @param place
     * @param type
     */
    void addMarker(Place place, int type) {
        MapClusterItem clusterItem = new MapClusterItem(place);
        PlacesHolder.getInstance().getAll(type).add(new PlaceWithCoord(place, clusterItem));
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
    }

    /**
     * Remove all markers of a specific category from list and from cluster manager
     *
     * @param category
     * @param toCluster
     */
    void clearMarkers(final int category, final boolean toCluster) {
        if (category == -1) {
            return;
        }
        for (PlaceWithCoord m : PlacesHolder.getInstance().getShown(category)) {
            try {
                map.clusterManager.removeItem(m.coordinates);
            } catch (Exception ignored) {
            }
        }
        PlacesHolder.getInstance().getShown(category).clear();
        if (map.clusterManager != null) {
            map.parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (toCluster) {
                        map.clusterManager.cluster();
                    }
                    map.parentActivity.bottomSheet
                            .updateList(category, PlacesHolder.getInstance().getShown(category));
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
        //TODO move the code we need to another method because now it will not be inited if user denies location
        map.adapter.onMapReady(map.mMap);
    }

    void loadAllPlaces() {
        map.parentActivity.searchBar.setProgressBarColor(SearchBarView.PROGRESS_BAR_BLUE);
        map.parentActivity.searchBar.showProgressBar();
        for (int i = 0; i < 1; i++) {
            PlacesHolder.getInstance().getAll(i).clear();
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
