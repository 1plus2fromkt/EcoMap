package com.twofromkt.ecomap.place_types;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.twofromkt.ecomap.data_struct.Pair;
import com.twofromkt.ecomap.util.TextUtil;

import static com.twofromkt.ecomap.util.LocationUtil.fromLatLngZoom;
import static com.twofromkt.ecomap.util.LocationUtil.getLatLng;
import static com.twofromkt.ecomap.util.LocationUtil.latLngToPair;
import static com.twofromkt.ecomap.util.Util.*;

import java.io.Serializable;

import static com.twofromkt.ecomap.util.LocationUtil.latLngToPair;
import static com.twofromkt.ecomap.util.Util.Period;
import static com.twofromkt.ecomap.util.Util.Timetable;

public abstract class Place implements Serializable {

    public static final int TRASHBOX = 0, ECOMOBILE = 1, OTHER = 2;
    public static final int ID = 0, LAT_DB = 1, LNG_DB = 2, RATE = 3, TITLE = 4, CONTENT = 5,
            ADDRESS = 6, IMG = 7, INFO = 8, WORK_TIME = 9, SITE = 10, TEL = 11, EMAIL = 12;

    @NonNull
    Pair<Double, Double> location;
    private int id;
    @NonNull
    private String name;
    protected String website, address;
    private double rate;
    private int categoryNumber;
    private Timetable workTime;
    @Nullable
    private String imgLink;
    private String information;
    private String phone;

    /**
     * Shows if all data or only the most important data is loaded
     */
    public boolean lite;

    Place(@NonNull String name, LatLng location, double rate, String information, Timetable workTime,
          String imgLink, String website, String phone, boolean lite) {
        this.name = name;
        this.location = latLngToPair(location);
        if (workTime != null) {
            this.workTime = new Timetable(workTime);
        }
        this.imgLink = imgLink;
        this.rate = rate;
        this.website = website;
        this.information = information;
        this.phone = phone;
        this.lite = lite;
    }

    Place(Cursor cursor, boolean lite) {
        this.id = cursor.getInt(ID);
        location = new Pair<>(cursor.getDouble(LAT_DB), cursor.getDouble(LNG_DB));
        name = cursor.getString(TITLE);
        this.lite = lite;
        if (lite) {
            return;
        }
        address = cursor.getString(ADDRESS);
        rate = cursor.getDouble(RATE);
        website = cursor.getString(SITE);
        website = TextUtil.formatLink(website);
        information = cursor.getString(INFO);
        imgLink = cursor.getString(IMG);
        workTime = new Timetable(cursor.getString(WORK_TIME));
        phone = cursor.getString(TEL);
        phone = TextUtil.formatPhone(phone);
    }

    protected Place() {
        location = new Pair<>(0.0, 0.0);
        name = "";
        categoryNumber = 0;
        workTime = null;
        imgLink = "";
        information = "";
    }

    public Place(Place e) {
        this(e.getName(), getLatLng(e.getLocation()), e.getRate(), e.getInformation(), e.getWorkTime(),
                e.getImgLink(), e.getWebsite(), e.getPhone(), true);
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

    public String getPhone() {
        return phone;
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

    protected void setAddress(String address) {
        this.address = address;
    }
}