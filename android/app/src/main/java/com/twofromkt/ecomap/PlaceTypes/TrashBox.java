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
        String[] arr = c.getString(CONTENT).replace(" ", "").split(",");
        for (String s : arr) {
            category.add(Category.fromIndex(Category.numFromName(s)));
        }
    }

    public TrashBox(String name, LatLng location, double rate, String information, Timetable workTime,
                    String imgLink, Set<Category> cat, String website) {
        super(name, location, rate, information, workTime, imgLink, website);
        category = new HashSet<>(cat);
        categoryNumber = TRASHBOX;
    }

    public enum Category {
        PAPER(0), GLASS(1), PLASTIC(2), METAL(3), CLOTHES(4), OTHER(5), DANGEROUS(6),
        BATTERY(7), BULB(8), APPLIANCES(9), TETRA_PACK(10);
        static final String[] names = {"Бумага", "Стекло", "Пластик", "Металл", "Одежда",
                                "Иное", "Опасные отходы", "Батарейки", "Лампочки",
                                "Бытовая техника", "Тетра Пак"};
        int n;

        public static int numFromName(String s) {
            for (int i = 0; i < names.length; i++) {
                if (names[i].contains(s) || s.contains(names[i]))
                    return i;
            }
            return -1;
        }

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