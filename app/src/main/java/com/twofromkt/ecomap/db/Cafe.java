package com.twofromkt.ecomap.db;

import com.google.android.gms.maps.model.LatLng;
import static com.twofromkt.ecomap.Util.*;

public class Cafe extends Place {
    String website;
    String menuLink;
    String phone;
    Cafe (LatLng location, String information, Period[] workTime, String img_link, String phone,
          String menuLink, String website) {
        super (location, information, workTime, img_link);
        this.phone = phone;
        this.menuLink = menuLink;
        this.website = website;
    }
}
