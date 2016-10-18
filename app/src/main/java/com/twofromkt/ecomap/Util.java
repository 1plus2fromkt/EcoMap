package com.twofromkt.ecomap;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.wallet.Cart;
import com.twofromkt.ecomap.data_struct.Pair;
import com.twofromkt.ecomap.db.Place;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;

public class Util {
    static HashMap<Marker, Place> markersToPlace = new HashMap<>();
    public static final double RADIUS = 6371e3;
    static ArrayList<Marker> activeMarkers = new ArrayList<>();

    public static double distanceLatLng(LatLng x, LatLng y) {

        double fi1 = x.latitude;
        double fi2 = y.latitude;
        double dfi = Math.toRadians(fi1 - fi2);
        double lambda = Math.toRadians(x.longitude - y.longitude);
        double a = Math.abs(Math.pow(Math.sin(dfi / 2), 2) +
                Math.cos(fi1) * Math.cos(fi2) * Math.sin(lambda / 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RADIUS * c;
    }

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

    public static LatLng fromPair(Pair<Double, Double> x) {
        return new LatLng(x.val1, x.val2);
    }
    public static LatLng fromPair(double lat, double lng) {
        return new LatLng(lat, lng);
    }


    public static Pair<Double, Double> fromLatLng(LatLng x) {
        return new Pair<>(x.latitude, x.longitude);
    }

    public static class Cartesian {
        public double x, y, z;

        public Cartesian(double lat, double lng) {
            x = RADIUS * Math.cos(lat) * Math.cos(lng);
            y = RADIUS * Math.cos(lat) * Math.sin(lng);
            z = RADIUS * Math.sin(lat);
        }

        public Cartesian(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Cartesian() {
        }

        public LatLng toLatLng() {
            double lat = Math.asin(z / RADIUS);
            double lng = Math.atan2(y, x);
            return new LatLng(lat, lng);
        }

        public void add(Cartesian x) {
            this.x += x.x;
            this.y += x.y;
            this.z += x.z;
        }

        public void mul(double a) {
            this.x *= a;
            this.y *= a;
            this.z *= a;
        }
    }

    public static Pair<LatLng, Double> center(ArrayList<LatLng> locations) {
        Cartesian sum = new Cartesian();
        for (LatLng l : locations) {
            sum.add(new Cartesian(l.latitude, l.longitude));
        }
        sum.mul(1 / (double) (locations.size()));
        LatLng center = sum.toLatLng();
        double maxs = 0;
        for (LatLng l : locations) {
            maxs = Math.max(maxs, distanceLatLng(l, center));
        }
        return new Pair<>(center, maxs);
    }

    public static CameraPosition fromLatLngZoom(double a, double b, float z) {
        return CameraPosition.fromLatLngZoom(new LatLng(a, b), z);
    }

    public static CameraPosition fromLatLngZoom(LatLng x, float z) {
        return fromLatLngZoom(x.latitude, x.longitude, z);
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

    public static Location getLocation(LocationManager manager, Criteria criteria) {
        return manager.getLastKnownLocation(manager.getBestProvider(criteria, false));
    }
}
