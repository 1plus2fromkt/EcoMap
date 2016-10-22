package com.twofromkt.ecomap.util;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.twofromkt.ecomap.db.Place;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;

public class Util {
    public static HashMap<Marker, Place> markersToPlace = new HashMap<>();
    public static ArrayList<Marker> activeMarkers = new ArrayList<>();
    public static ArrayList<Place> searchResults = new ArrayList<>(); // We definitely shouldn't do this

    public static class Period {
        public Time open, close;
    }

    public static class AppendingObjectOutputStream extends ObjectOutputStream {

        public AppendingObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            reset();
        }

    }

    public static LatLngBounds includeAll(ArrayList<LatLng> pos) {
        if (pos.size() == 0) {
            throw new IllegalArgumentException("Cannot get bounds of empty positions list");
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        final double margin = .05;
        LatLng top, bottom, left, right;
        top = bottom = left = right = pos.get(0);
        for (LatLng ll : pos) {
            if (top.latitude < ll.latitude) top = ll;
            if (bottom.latitude > ll.latitude) bottom = ll;
            if (left.longitude < ll.longitude) left = ll;
            if (right.longitude > ll.longitude) right = ll;
        }
        top = new LatLng(top.latitude + margin, top.longitude);
        bottom = new LatLng(bottom.latitude - margin, bottom.longitude);
        left = new LatLng(left.latitude, left.longitude - margin);
        right = new LatLng(right.latitude, right.longitude + margin);
        builder.include(top);
        builder.include(bottom);
        builder.include(left);
        builder.include(right);
        return builder.build();
    }

    public static void moveMap(GoogleMap map, CameraPosition pos) {
        map.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
    }
}
