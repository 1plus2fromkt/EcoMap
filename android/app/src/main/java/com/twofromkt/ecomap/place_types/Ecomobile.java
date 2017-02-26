package com.twofromkt.ecomap.place_types;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.twofromkt.ecomap.data_struct.Date;

import java.util.ArrayList;
import java.util.List;


public class Ecomobile extends Place implements Comparable<Ecomobile>{

    private List<Pair<Date, Date>> time;
    private String district;
    private final static int ADDRESS = 0, DISTRICT = 1, TIMETABLE = 2;

    @Override
    public int compareTo(@NonNull Ecomobile o) {
        return time.get(0).first.compareTo(o.time.get(0).first);
    }

    private Ecomobile(Ecomobile e, Pair<Date, Date> d) {
        super(e);
        time = new ArrayList<>();
        time.add(d);
    }

    public Ecomobile(Cursor c) {
        setAddress(c.getString(ADDRESS));
        this.district = c.getString(DISTRICT);
        String[] t = c.getString(TIMETABLE).split("[|]");
        time = new ArrayList<>();
        for (String s : t) {
            String[] d = s.split("[ ]");
            String[] times = d[1].split("[-]");

            time.add(new Pair<>(new Date(d[0] + " " + times[0]),
                    new Date(d[0] + " " + times[1])));
        }
    }

    public List<Ecomobile> split() {
        List<Ecomobile> l = new ArrayList<>();
        for (Pair<Date, Date> d : time) {
            l.add(new Ecomobile(this, d));
        }
        return l;
    }

    public String getPeriod() {
        return time.get(0).first.toString() + " - " + time.get(0).second.toString();
    }
}
