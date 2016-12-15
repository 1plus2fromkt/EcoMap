package com.twofromkt.ecomap.place_types;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.twofromkt.ecomap.data_struct.Pair;

import static com.twofromkt.ecomap.util.LocationUtil.latLngToPair;
import static com.twofromkt.ecomap.util.Util.*;

import java.io.Serializable;

public abstract class Place implements Serializable {

    public static final int TRASHBOX = 0, CAFE = 1, OTHER = 2;
    public static final int ID = 0, LAT_DB = 1, LNG_DB = 2, RATE = 3, TITLE = 4, CONTENT = 5,
            ADDRESS = 6, IMG = 7, INFO = 8, WORK_TIME = 9, SITE = 10, TEL = 11, EMAIL = 12;


    @NonNull
    private Pair<Double, Double> location;
    private int id;
    @NonNull
    private String name;
    private String website, address;
    private double rate;
    private int categoryNumber;
    private Timetable workTime;
    @Nullable
    private String imgLink;
    private String information;

    Place(@NonNull String name, LatLng location, double rate, String information, Timetable workTime,
          String imgLink, String website) {
        this.name = name;
        this.location = latLngToPair(location);
        if (workTime != null)
            this.workTime = new Timetable(workTime);
        this.imgLink = imgLink;
        this.rate = rate;
        this.website = website;
        this.information = information;
    }

    Place(Cursor cursor, boolean lite) {
        this.id = cursor.getInt(ID);
        location = new Pair<>(cursor.getDouble(LAT_DB), cursor.getDouble(LNG_DB));
        name = cursor.getString(TITLE);
        if (lite)
            return;
        address = cursor.getString(ADDRESS);
        rate = cursor.getDouble(RATE);
        website = cursor.getString(SITE);
        information = cursor.getString(INFO);
        imgLink = cursor.getString(IMG);
        workTime = new Timetable(cursor.getString(WORK_TIME));
    }

    protected Place() {
        location = new Pair<>(0.0, 0.0);
        name = "";
        categoryNumber = 0;
        workTime = null;
        imgLink = "";
        information = "";
    }

    boolean isOpened(Period.Time t, int dayOfWeek) {
        Period.Time a = workTime.getTable()[dayOfWeek].getOpen(),
                b = workTime.getTable()[dayOfWeek].getClose();
        boolean bef = t.before(b), aft = t.after(a);
        if (a.before(b)) {
            return (bef && aft);
        } else {
            return (bef || aft);
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Place && equals((Place) o);
    }

    public boolean equals(Place p) { //TODO check work time and image link too
        return location.equals(p.location) && name.equals(p.name);
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getWebsite() {
        return website;
    }

    public String getAddress() {
        return address;
    }

    public String getImgLink() {
        return imgLink;
    }

    public String getInformation() {
        return information;
    }

    public Pair<Double, Double> getLocation() {
        return location;
    }

    public double getRate() {
        return rate;
    }

    public int getCategoryNumber() {
        return categoryNumber;
    }

    public Timetable getWorkTime() {
        return workTime;
    }

    protected void setCategoryNumber(int x) {
        categoryNumber = x;
    }
}