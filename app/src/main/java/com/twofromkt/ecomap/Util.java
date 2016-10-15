package com.twofromkt.ecomap;

import com.google.android.gms.maps.model.LatLng;
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
    public static final double R = 6371e3;
    static ArrayList<Marker> currMarkers = new ArrayList<>();
    public static double distanceLatLng(LatLng x, LatLng y) {

        double fi1 = x.latitude;
        double fi2 = y.latitude;
        double dfi = Math.toRadians(fi1 - fi2);
        double lambda = Math.toRadians(x.longitude - y.longitude);
        double a = Math.abs(Math.pow(Math.sin(dfi / 2), 2) +
                Math.cos(fi1) * Math.cos(fi2) * Math.sin(lambda / 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
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

    public static Pair<Double, Double> fromLatLng(LatLng x) {
        return new Pair<>(x.latitude, x.longitude);
    }

    public static class Cartesian {
        public double x, y, z;
        public Cartesian(double lat, double lng) {
            x = R * Math.cos(lat) * Math.cos(lng);
            y = R * Math.cos(lat) * Math.sin(lng);
            z = R * Math.sin(lat);
        }
        public Cartesian(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        public Cartesian()
        {}
        public LatLng toLatLng() {
            double lat = Math.asin(z / R);
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

    public static Pair<LatLng, Double> center(ArrayList<LatLng> locations){
        Cartesian sum = new Cartesian(), c;
        for (LatLng l : locations) {
            sum.add(new Cartesian(l.latitude, l.longitude));
        }
        sum.mul(1 / (double)(locations.size()));
        LatLng center = sum.toLatLng();
        double maxs = 0;
        for (LatLng l : locations) {
            maxs = Math.max(maxs, distanceLatLng(l, center));
        }
        return new Pair<>(center, maxs);
    }
}
