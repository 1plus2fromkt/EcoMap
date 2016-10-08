package com.twofromkt.ecomap;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;

public class Util {
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
        Time open, close;
    }
}