package com.twofromkt.ecomap.db;

import com.google.android.gms.maps.model.LatLng;
import static com.twofromkt.ecomap.Util.*;

import java.io.Serializable;
import java.sql.Time;

abstract class Place implements Serializable {
    public LatLng location;
    String name;
    Period[] workTime;
    String img_link;
    public String information;
    Place (String name, LatLng location, String information, Period[] workTime, String img_link) {
        this.name = name;
        this.location = location;
        this.information = information;
        if (workTime != null)
            System.arraycopy(workTime, 0, this.workTime, 0, workTime.length);
        this.img_link = img_link;
    }


    boolean isOpened(Time t, int dayOfWeek) {
        Time a = workTime[dayOfWeek].open, b = workTime[dayOfWeek].close;
        boolean bef = t.before(b), aft= t.after(a);
        if (a.before(b))
            return (bef && aft);
        else
            return (bef || aft);

    }
}
