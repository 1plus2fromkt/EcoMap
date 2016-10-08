package com.twofromkt.ecomap.db;

import com.google.android.gms.maps.model.LatLng;
import static com.twofromkt.ecomap.Util.*;

public class TrashBox extends Place {
    enum Category {GLASS, AND, TIME};
    Category category;
    TrashBox(LatLng location, String information, Period[] workTime, String img_link, Category cat) {
        super(location, information, workTime, img_link);
        category = cat;
    }
}
