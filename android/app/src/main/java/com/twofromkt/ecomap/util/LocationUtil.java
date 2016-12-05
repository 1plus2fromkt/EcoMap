package com.twofromkt.ecomap.util;

import android.location.Address;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.twofromkt.ecomap.data_struct.Pair;

public class LocationUtil {
    public static final double RADIUS = 6371e3;

    public static double distanceLatLng(LatLng x, LatLng y) {
        double fi1 = Math.toRadians(x.latitude);
        double fi2 = Math.toRadians(y.latitude);
        double dfi = fi1 - fi2;
        double lambda = Math.toRadians(x.longitude - y.longitude);
        double a = Math.abs(Math.pow(Math.sin(dfi / 2), 2) +
                Math.cos(fi1) * Math.cos(fi2) * Math.pow(Math.sin(lambda / 2), 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RADIUS * c;
    }

    public static LatLng getLatLng(Pair<Double, Double> x) {
        return new LatLng(x.val1, x.val2);
    }

    public static LatLng getLatLng(double lat, double lng) {
        return new LatLng(lat, lng);
    }

    public static LatLng getLatLng(Address a) {
        return new LatLng(a.getLatitude(), a.getLongitude());
    }

    public static Pair<Double, Double> latLngToPair(LatLng x) {
        return new Pair<>(x.latitude, x.longitude);
    }

    public static CameraPosition fromLatLngZoom(double a, double b, float z) {
        return CameraPosition.fromLatLngZoom(new LatLng(a, b), z);
    }

    public static CameraPosition fromLatLngZoom(LatLng x, float z) {
        return fromLatLngZoom(x.latitude, x.longitude, z);
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
}
