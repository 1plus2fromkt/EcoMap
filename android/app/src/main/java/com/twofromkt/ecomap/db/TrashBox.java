package com.twofromkt.ecomap.db;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.twofromkt.ecomap.map_activity.MapActivity;

import java.util.HashSet;
import java.util.Set;

import static com.twofromkt.ecomap.util.Util.*;

public class TrashBox extends Place {

    @NonNull
    HashSet<Category> category;

    public TrashBox(String name, LatLng location, String information, Period[] workTime, String imgLink, Set<Category> cat) {
        super(name, location, information, workTime, imgLink);
        category = new HashSet<>(cat);
        categoryNumber = Place.TRASHBOX;
    }

    public enum Category {
        CLOTHES(0), PAPER(1), PLASTIC(2), METAL(3);
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
