package com.twofromkt.ecomap.db;

import com.google.android.gms.maps.model.LatLng;
import static com.twofromkt.ecomap.Util.*;

import java.io.Serializable;

abstract class Place implements Serializable {
    LatLng location;
    Period[] workTime;
    String img_link;
    String information;
    Place (LatLng location, String information, Period[] workTime, String img_link) {
        this.location = location;
        this.information = information;
        System.arraycopy(workTime, 0, this.workTime, 0, workTime.length);
        this.img_link = img_link;
    }
}
