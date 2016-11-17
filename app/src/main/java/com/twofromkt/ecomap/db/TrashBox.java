package com.twofromkt.ecomap.db;

import com.google.android.gms.maps.model.LatLng;
import com.twofromkt.ecomap.map_activity.MapActivity;

import java.util.HashSet;
import java.util.Set;

import static com.twofromkt.ecomap.util.Util.*;

public class TrashBox extends Place {

    HashSet<Category> category;

    public TrashBox(String name, LatLng location, String information, Period[] workTime, String img_link, Set<Category> cat) {
        super(name, location, information, workTime, img_link);
        category = new HashSet<>(cat);
        category_number = MapActivity.TRASH_NUM;
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
