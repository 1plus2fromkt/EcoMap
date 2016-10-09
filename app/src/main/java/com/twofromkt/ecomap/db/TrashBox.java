package com.twofromkt.ecomap.db;

import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

import static com.twofromkt.ecomap.Util.*;

public class TrashBox extends Place {

    Category category;

    public TrashBox(String name, LatLng location, String information, Period[] workTime, String img_link, Category cat) {
        super(name, location, information, workTime, img_link);
        category = cat;
    }

    public enum Category {
        GLASS(0), AND(1), TIME(2);
        int n;

        Category(int n) {
            this.n = n;
        }

        static Category fromIndex(int i) {
            for (Category e : Category.values()) {
                if (e.n == i)
                    return e;
            }
            return null;
        }
    }
}
