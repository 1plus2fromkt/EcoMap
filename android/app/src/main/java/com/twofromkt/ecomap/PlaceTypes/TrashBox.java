package com.twofromkt.ecomap.PlaceTypes;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashSet;
import java.util.Set;

import static com.twofromkt.ecomap.util.Util.*;

public class TrashBox extends Place {


    @NonNull
    public HashSet<Category> category;

    public TrashBox(Cursor c) {
        super(c);
        name = c.getString(TITLE);
        information = c.getString(ADDRESS) + " " + c.getString(INFO);
        category = new HashSet<>();
        category.add(Category.GLASS);
        category.add(Category.AND); //TODO: REPLACE THIS!
    }

    public TrashBox() {
        category = new HashSet<>();
    }

    public TrashBox(String name, LatLng location, String information, Period[] workTime, String imgLink, Set<Category> cat) {
        super(name, location, information, workTime, imgLink);
        category = new HashSet<>(cat);
        categoryNumber = TRASHBOX;
    }

    public enum Category {
        GLASS(0), AND(1), TIME(2);
        int n;

        Category(int n) {
            this.n = n;
        }

        public static Category fromIndex(int i) {
            for (Category e : Category.values()) {
                if (e.n == i)
                    return e;
            }
            return null;
        }
    }
}
