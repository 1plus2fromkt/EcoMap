package com.twofromkt.ecomap.data_struct;

import com.twofromkt.ecomap.util.Util;

import java.util.Comparator;

import static java.util.Comparator.comparingInt;

/**
 * Created by nikita on 2/26/17.
 */

public class Date implements Comparable<Date>{
    private int year, month, day;
    private Util.Period period;
    public Date(String s) {
        String[] a = s.split("[ ]"), b = a[0].split("[.]");
        period = new Util.Period(a[1]);
        day = Integer.parseInt(b[0]);
        month = Integer.parseInt(b[1]);
        year = Integer.parseInt(b[2]);
    }

    @Override
    public int compareTo(Date o) {
        if (year != o.year)
            return year - o.year;
        if (month != o.month)
            return month - o.month;
        if (day != o.day)
            return day - o.day;
        return period.compareTo(o.period);
    }
}
