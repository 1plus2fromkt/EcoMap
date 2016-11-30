package com.twofromkt.ecomap.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;

import com.android.internal.util.Predicate;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.twofromkt.ecomap.PlaceTypes.Cafe;
import com.twofromkt.ecomap.PlaceTypes.Place;
import com.twofromkt.ecomap.PlaceTypes.TrashBox;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.twofromkt.ecomap.Consts.CAFE_NUMBER;
import static com.twofromkt.ecomap.Consts.FILE_NAMES;
import static com.twofromkt.ecomap.Consts.TRASH_NUMBER;
import static com.twofromkt.ecomap.util.LocationUtil.distanceLatLng;
import static com.twofromkt.ecomap.util.LocationUtil.getLatLng;
import static com.twofromkt.ecomap.util.Util.*;

public class GetPlaces extends AsyncTaskLoader<ResultType> {
    public static final int NEAR = 0, ALL = 1;
    public static final String WHICH_PLACE = "WHICH", RADIUS = "RADIUS", CHOSEN = "CHOSEN",
                                LAT = "LAT", LNG = "LNG", MODE = "MODE";

    private int which, mode;
    private boolean[] chosen;
    private double lat, lng;
    private float radius;

    public GetPlaces(Context context, Bundle args) {
        super(context);
        if (args != null) {
            which = args.getInt(WHICH_PLACE);
            mode = args.getInt(MODE);
            if (which == TRASH_NUMBER) { //and maybe && NEAR
                chosen = args.getBooleanArray(CHOSEN);
            }
            if (mode == NEAR) {
                lat = args.getDouble(LAT);
                lng = args.getDouble(LNG);
                radius = args.getFloat(RADIUS);
            }
        }
    }

    private interface MyFactory<T> {
        T init(Cursor c);
    }

    private static class CafeFactory implements MyFactory<Cafe> {
        @Override
        public Cafe init(Cursor c) {
            return new Cafe(c);
        }
    }

    private static class TrashFactory implements MyFactory<TrashBox> {

        @Override
        public TrashBox init(Cursor c) {
            return new TrashBox(c);
        }
    }

    private static <T extends Place> ArrayList<T> getPlaces(Predicate<T> pr, int category,
                                                            Context context, MyFactory<T> fac) {
        ArrayList<T> ans = new ArrayList<>();

        try(SQLiteDatabase db = SQLiteDatabase.openDatabase(new File(context.getFilesDir(),
                DBAdapter.getPathToDb(category)).getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
            Cursor cur = db.rawQuery("SELECT * FROM " + DBAdapter.tableName + ";", null)) {
            cur.moveToFirst();
            T x;
            while (cur.moveToNext()) {
                x = fac.init(cur);
//                if (pr.apply(x))
                    ans.add(x);
            }
        } catch (SQLiteCantOpenDatabaseException e) {
            e.printStackTrace();
            //TODO: send no database message and maybe update it
        }
        return ans;
    }

    public static ArrayList<Cafe> getCafes(final LatLng x, final double radius, Context context) {

        return getPlaces(new Predicate<Cafe>() {
            @Override
            public boolean apply(Cafe o) {
                return distanceLatLng(x, getLatLng(o.location)) < radius;
            }
        }, CAFE_NUMBER, context, new CafeFactory());
    }

    public static ArrayList<TrashBox> getTrashes(final LatLng x, final double radius,
                                                 boolean[] arr, Context context) {
        Set<TrashBox.Category> s = new HashSet<>();
        for (int i = 0; i < arr.length; i++)
            if (arr[i])
                s.add(TrashBox.Category.fromIndex(i));
        final Set<TrashBox.Category> finalS = s;
        return getPlaces(new Predicate<TrashBox>() {
            @Override
            public boolean apply(TrashBox o) {
                finalS.retainAll(o.category);
                return distanceLatLng(x, getLatLng(o.location)) < radius && finalS.size() > 0;
            }
        }, TRASH_NUMBER, context, new TrashFactory());
    }

    @Override
    public void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public ResultType loadInBackground() {
        ArrayList<? extends Place> ans = new ArrayList<>();
        switch (which) {
            case TRASH_NUMBER:
                switch (mode) {
                    case NEAR:
                        ans = getTrashes(getLatLng(lat, lng), radius, chosen, getContext());
                        break;
                }
                break;
            case CAFE_NUMBER:
                switch (mode) {
                    case NEAR:
                        ans = getCafes(getLatLng(lat, lng), radius, getContext());
                        break;
                }
                break;
        }
        LatLngBounds bounds = includeAll(ans);
        CameraUpdate cu = null;
        if (ans.size() > 0)
            cu = CameraUpdateFactory.newLatLngBounds(bounds, 10); // WTF is 10?
        return new ResultType(cu, ans, which);
    }
}
