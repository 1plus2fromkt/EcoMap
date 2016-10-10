package com.twofromkt.ecomap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
    static ArrayList<Marker> currMarkers = new ArrayList<>();
    public static double distanceLatLng(LatLng x, LatLng y) {
        double fi1 = x.latitude;
        double fi2 = y.latitude;
        double dfi = Math.toRadians(fi1 - fi2);
        double lambda = Math.toRadians(x.longitude - y.longitude);
        double a = Math.pow(Math.sin(dfi / 2), 2) +
                Math.cos(fi1) * Math.cos(fi2) * Math.sin(lambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return a * c;
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
}
