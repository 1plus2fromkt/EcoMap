package com.twofromkt.ecomap.place_types;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.twofromkt.ecomap.data_struct.Date;
import com.twofromkt.ecomap.data_struct.Pair;
import com.twofromkt.ecomap.util.Util;

import java.util.ArrayList;
import java.util.List;

public class Ecomobile extends Place implements Comparable<Ecomobile> {

    private List<Pair<Date, Util.Period>> time;
    private String district;
    private final static int ADDRESS = 0, LAT = 1, LNG = 2, DISTRICT = 3, TIMETABLE = 4;

    @Override
    public int compareTo(@NonNull Ecomobile o) {
        if (time.get(0).val1.compareTo(o.time.get(0).val1) == 0) {
            return time.get(0).val2.compareTo(o.time.get(0).val2);
        }
        return time.get(0).val1.compareTo(o.time.get(0).val1);
    }

    private Ecomobile(Ecomobile e, Pair<Date, Util.Period> period) {
        super(e);
        lite = false;
        time = new ArrayList<>();
        time.add(period);
        address = e.address;
    }

    public Ecomobile(Cursor c) {
        lite = false;
        setAddress(c.getString(ADDRESS));
        double lat = c.getDouble(LAT);
        double lng = c.getDouble(LNG);
        location = new Pair<>(lat, lng);
        this.district = c.getString(DISTRICT);
        String[] t = c.getString(TIMETABLE).split("[|]");
        time = new ArrayList<>();
        for (String s : t) {
            s = s.trim();
            if (s.equals("")) {
                continue;
            }
            String[] d = s.split("[ ]");

            time.add(new Pair<>(new Date(d[0]), new Util.Period(d[1])));
        }
    }

    public List<Ecomobile> split() {
        List<Ecomobile> l = new ArrayList<>();
        for (Pair<Date, Util.Period> d : time) {
            l.add(new Ecomobile(this, d));
        }
        return l;
    }

    public String getPeriod() {
        return time.get(0).val1.toString() + ",  " + time.get(0).val2.toString();
    }
}
