package com.twofromkt.ecomap.PlaceTypes;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static com.twofromkt.ecomap.Consts.TRASH_TYPES_NUMBER;
import static com.twofromkt.ecomap.util.Util.*;

public class TrashBox extends Place implements Serializable {

    boolean[] category;

    public TrashBox(Cursor c) {
        super(c);
        name = c.getString(TITLE);
        information = c.getString(INFO);
        category = new boolean[TRASH_TYPES_NUMBER];
        String[] arr = c.getString(CONTENT).replace(", ", ",").split(",");
        for (String s : arr) {
            int catIndex = Category.numFromName(s);
            if (catIndex != -1) {
                category[Category.numFromName(s)] = true;
            } else {
                Log.d("TrashBoxInit", "String " + s + " is not a known category!");
            }
        }
    }

    public TrashBox(String name, LatLng location, double rate, String information, Timetable workTime,
                    String imgLink, Set<Category> cat, String website) {
        super(name, location, rate, information, workTime, imgLink, website);
        category = new boolean[TRASH_TYPES_NUMBER];
        for (Category currCat : cat) {
            category[currCat.n] = true;
        }
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
                if (names[i].contains(s) || s.contains(names[i])) {
                    return i;
                }
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

        public static String nameFromIndex(int i) {
            return names[i];
        }
    }
}