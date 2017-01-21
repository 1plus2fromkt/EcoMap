package com.twofromkt.ecomap.map_activity.map;

import com.twofromkt.ecomap.util.Util;

import java.util.ArrayList;

import static com.twofromkt.ecomap.Consts.CATEGORIES_NUMBER;

/**
 * This is a singleton to store data about places that are currently loaded.
 * In each list we have a sublist for each category we have.
 */
final class PlacesHolder {
    private volatile ArrayList<ArrayList<Util.PlaceWithCoord>> allMarkers, shownMarkers;

    private static PlacesHolder ourInstance = new PlacesHolder();

    static PlacesHolder getInstance() {
        return ourInstance;
    }

    private PlacesHolder() {
        allMarkers = new ArrayList<>();
        shownMarkers = new ArrayList<>();
        for (int i = 0; i < CATEGORIES_NUMBER; i++) {
            allMarkers.add(new ArrayList<Util.PlaceWithCoord>());
            shownMarkers.add(new ArrayList<Util.PlaceWithCoord>());
        }
    }

    ArrayList<ArrayList<Util.PlaceWithCoord>> getShown() {
        return shownMarkers;
    }

    ArrayList<ArrayList<Util.PlaceWithCoord>> getAll() {
        return allMarkers;
    }

    ArrayList<Util.PlaceWithCoord> getShown(int categoryNumber) {
        return shownMarkers.get(categoryNumber);
    }

    ArrayList<Util.PlaceWithCoord> getAll(int categoryNumber) {
        return allMarkers.get(categoryNumber);
    }
}
