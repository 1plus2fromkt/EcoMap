package com.twofromkt.ecomap.PlaceTypes;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import static com.twofromkt.ecomap.util.Util.*;

public class Cafe extends Place implements Serializable {

    String menuLink;
    String phone;

    public Cafe() {
        menuLink = "";
        phone = "";
    }

    public Cafe(Cursor c) {
        super(c);
    }

    public Cafe(String name, LatLng location, double rate, String information, Timetable workTime,
                String webSite, String phone, String menuLink, String website) {
        super (name, location, rate, information, workTime, website, website);
        this.phone = phone;
        this.menuLink = menuLink;
        categoryNumber = Place.CAFE;
    }
}
