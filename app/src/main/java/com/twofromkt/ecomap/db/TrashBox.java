package com.twofromkt.ecomap.db;

import com.google.android.gms.maps.model.LatLng;
import static com.twofromkt.ecomap.Util.*;

public class TrashBox extends Place {

    Category category;

    public TrashBox(LatLng location, String information, Period[] workTime, String img_link, Category cat) {
        super(location, information, workTime, img_link);
        category = cat;
    }

    public enum Category { GLASS, AND, TIME }
}
