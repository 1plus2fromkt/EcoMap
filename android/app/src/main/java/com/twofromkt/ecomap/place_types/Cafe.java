package com.twofromkt.ecomap.place_types;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import static com.twofromkt.ecomap.util.Util.*;

public class Cafe extends Place implements Serializable {

    private String menuLink;
    private String phone;

    public Cafe(Cursor c, boolean lite) {
        super(c, lite);
    }

    public Cafe(String name, LatLng location, double rate, String information, Timetable workTime,
                String webSite, String phone, String menuLink, String imgLink) {
        super(name, location, rate, information, workTime, imgLink, webSite, true);
        this.phone = phone;
        this.menuLink = menuLink;
        setCategoryNumber(CAFE);
    }
}
