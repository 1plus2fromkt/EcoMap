package com.twofromkt.ecomap.PlaceTypes;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.twofromkt.ecomap.data_struct.Pair;

import static com.twofromkt.ecomap.util.LocationUtil.latLngToPair;
import static com.twofromkt.ecomap.util.Util.*;

import java.io.Serializable;
import java.sql.Time;

public abstract class Place implements Serializable {

    public static final int TRASHBOX = 0, CAFE = 1, OTHER = 2;
    protected static final int ID = 0, LAT_DB = 1, LNG_DB = 2, RATE = 3, TITLE = 4, CONTENT = 5,
            ADDRESS = 6, IMG = 7, INFO = 8, WORK_TIME = 9, SITE = 10, TEL = 11, EMAIL = 12;


    @NonNull
    public Pair<Double, Double> location;
    @NonNull
    public String name;
    public String address;
    public int categoryNumber;
    Period[] workTime;
    @Nullable
    String imgLink;
    public String information;

    Place(String name, LatLng location, String information, Period[] workTime, String imgLink) {
        this.name = name;
        this.location = latLngToPair(location);
        this.information = information;
        if (workTime != null)
            System.arraycopy(workTime, 0, this.workTime, 0, workTime.length);
        this.imgLink = imgLink;
    }

    Place (Cursor cursor) {
        location = new Pair<>(cursor.getDouble(LAT_DB), cursor.getDouble(LNG_DB));
        imgLink = "";
    }

    protected Place() {
        location = new Pair<>(0.0, 0.0);
        name = "";
        categoryNumber = 0;
        workTime = null;
        imgLink = "";
        information = "";
    }

    boolean isOpened(Time t, int dayOfWeek) {
        Time a = workTime[dayOfWeek].open, b = workTime[dayOfWeek].close;
        boolean bef = t.before(b), aft = t.after(a);
        if (a.before(b)) {
            return (bef && aft);
        } else {
            return (bef || aft);
        }
    }
}
