package com.twofromkt.ecomap.util;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.twofromkt.ecomap.PlaceTypes.Place;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Util {

    public static class Timetable implements Serializable {
        private static final String prefix = "0 ";
        private static final int DAYS_IN_WEEK = 7;
        public Period[] table;
        public Timetable (Timetable rhs) {
            System.arraycopy(rhs.table, 0, table, 0, table.length);
        }

        public Timetable (String s) {
            table = new Period[DAYS_IN_WEEK];
            if (s.startsWith(prefix) || s.equals("")) {
                table[0] = new Period(s, false);
            } else {
                try {
                    String[] arr = s.split("[\\$]");
                    for (int i = 0; i < DAYS_IN_WEEK; i++) {
                        table[i] = new Period(arr[i], true);
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        }
    }

    public static class Period implements Serializable {
        public Time open, close;
        String time;
        boolean isTimetable;
        public Period (String s, boolean isTimetable){
            this.isTimetable = isTimetable;
            if (!isTimetable) {
                time = s;
            } else {
                String[] arr = s.split("[#]");
                open = new Time(arr[0]);
                close = new Time(arr[1]);
            }
        }

        public static class Time {
            int h, m;
            Time (int h, int m) {
                this.h = h;
                this.m = m;
            }
            Time (String s) {
                String[] ar = s.split("[:]");
                h = Integer.parseInt(ar[0]);
                m = Integer.parseInt(ar[1]);
            }

            public boolean before(Time t) {
                return (h < t.h || (h == t.h && m <= t.m));
            }

            public boolean after(Time t) {
                return !before(t);
            }
        }
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

    @Nullable
    public static LatLngBounds includeAll(ArrayList<? extends Place> data) {
        ArrayList<LatLng> pos = new ArrayList<>();
        for (Place x : data) {
            pos.add(LocationUtil.getLatLng(x.location));
        }
        if (pos.size() == 0) {
            return null;
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

}
