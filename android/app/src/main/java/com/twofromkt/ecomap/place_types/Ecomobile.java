package com.twofromkt.ecomap.place_types;

import android.support.annotation.NonNull;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Ecomobile extends Place implements Comparable<Ecomobile>{

    private List<Pair<Date, Date>> time;

    @Override
    public int compareTo(@NonNull Ecomobile o) {
        return time.get(0).first.compareTo(o.time.get(0).first);
    }

    private Ecomobile(Ecomobile e, Pair<Date, Date> d) {
        super(e);
        time = new ArrayList<>();
        time.add(d);
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
