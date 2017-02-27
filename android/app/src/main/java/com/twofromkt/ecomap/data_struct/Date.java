package com.twofromkt.ecomap.data_struct;

import java.io.Serializable;

public class Date implements Serializable, Comparable<Date> {
    private int year, month, day;

    public Date(String s) {
        String[] a = s.split("[.]");
        day = Integer.parseInt(a[0]);
        month = Integer.parseInt(a[1]);
        year = Integer.parseInt(a[2]);
    }

    @Override
    public int compareTo(Date o) {
        if (year != o.year)
            return year - o.year;
        if (month != o.month)
            return month - o.month;
        return day - o.day;
    }

    public String toString() {
        return day + "." + month + "." + year;
    }
}
