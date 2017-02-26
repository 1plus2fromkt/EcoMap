package com.twofromkt.ecomap.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.twofromkt.ecomap.place_types.Cafe;
import com.twofromkt.ecomap.place_types.Place;
import com.twofromkt.ecomap.place_types.TrashBox;

import java.io.File;
import java.util.ArrayList;

import static com.twofromkt.ecomap.Consts.ECOMOBILE_ID;
import static com.twofromkt.ecomap.Consts.TRASH_ID;
import static com.twofromkt.ecomap.util.LocationUtil.getLatLng;
import static com.twofromkt.ecomap.util.Util.*;

public class PlacesLoader extends AsyncTaskLoader<PlaceResultType> {
    public static final int IN_BOUNDS = 0, ALL = 1, BY_ID = 2, ONE_MATCH = 0, ALL_MATCH = 1;
    public static final String WHICH_PLACE = "WHICH", CHOSEN = "CHOSEN",
            LAT_MINUS = "LATMINUS", LNG_MINUS = "LNGMINUS", MODE = "MODE",
            LAT_PLUS = "LATPLUS", LNG_PLUS = "LNGPLUS", ANY_MATCH_KEY = "OVERLAP",
            ANIMATE_MAP = "ANIMATE_MAP", LITE = "LITE", ID = "ID";
    private static final String TAG = "GETPLACES";

    private int which, mode, match, id;
    private boolean[] chosen;
    private double latMinus, lngMinus, latPlus, lngPlus;

    private boolean lite;

    private boolean isLoading, resultSuccess;

    public PlacesLoader(Context context, Bundle args) {
        super(context);
        if (args != null) {
            which = args.getInt(WHICH_PLACE);
            mode = args.getInt(MODE);
            lite = args.getBoolean(LITE);
            if (which == TRASH_ID && mode != ALL) {
                chosen = args.getBooleanArray(CHOSEN);
                match = args.getInt(ANY_MATCH_KEY);
            }
            if (mode == IN_BOUNDS) {
                latMinus = args.getDouble(LAT_MINUS);
                lngMinus = args.getDouble(LNG_MINUS);
                latPlus = args.getDouble(LAT_PLUS);
                lngPlus = args.getDouble(LNG_PLUS);
            } else if (mode == BY_ID) {
                id = args.getInt(ID);
            }
        }
    }

    private interface PlaceFactory<T> {
        T init(Cursor c, boolean lite);
    }

    private static class CafeFactory implements PlaceFactory<Cafe> {
        @Override
        public Cafe init(Cursor c, boolean lite) {
            return new Cafe(c, lite);
        }
    }

    private static class TrashFactory implements PlaceFactory<TrashBox> {
        @Override
        public TrashBox init(Cursor c, boolean lite) {
            return new TrashBox(c, lite);
        }
    }

    /**
     * Read places from database
     *
     * @param filter   filter for database queries
     * @param category type of places to read
     * @param context
     * @param fac      factory to create places
     * @param <T>      type of places
     * @return ArrayList of Place if place were read successfully, null otherwise
     */
    private <T extends Place> ArrayList<T> getPlaces(String filter, int category,
                                                     Context context, PlaceFactory<T> fac) {
        ArrayList<T> ans = new ArrayList<>();
        //TODO: CHANGE rate TO SOMETHING CLEVER
        String order = " ORDER BY rate ASC ", limit = " LIMIT ";
        try (SQLiteDatabase db = SQLiteDatabase.openDatabase(new File(context.getFilesDir(),
                DBAdapter.getPathToDb(category)).getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
             Cursor cur = db.rawQuery("SELECT * FROM " + DBAdapter.tableName + " " +
                     filter + /*+ order +*/ ";", null)) {

            T x;
            if (cur.moveToFirst()) {
                do {
                    x = fac.init(cur, lite);
                    ans.add(x);
                } while (cur.moveToNext());
            }
        } catch (SQLiteCantOpenDatabaseException e) {
            return null;
            //TODO: send no database message and maybe update it
        }
        return ans;
    }

    // Returns null in case of error!
    private ArrayList<Cafe> getEcomobiles(final LatLng x_minus, final LatLng x_plus, Context context) {
        String filter = "";
//        if (mode == IN_BOUNDS)
//            filter = sqlCoordBounds(x_minus, x_plus, CAFE_ID);
        return getPlaces(filter, ECOMOBILE_ID, context, new CafeFactory());
    }

    // Returns null in case of error!
    private ArrayList<TrashBox> getTrashes(final LatLng xMinus, final LatLng xPlus,
                                           Context context) {
        String filter = "";
//        if (mode == IN_BOUNDS) {
//            filter = sqlCoordBounds(xMinus, xPlus, TRASH_ID) + " AND (";
//            boolean added = false;
//            for (int i = 0; i < chosen.length; i++) {
//                if (chosen[i]) {
//                    if (added) {
//                        filter += (match == ONE_MATCH ? " OR " : " AND ");
//                    }
//                    filter += "(" + DBAdapter.getColumnName(TRASH_ID, Place.CONTENT)
//                            + " LIKE " + "\'%" + TrashBox.Category.nameFromIndex(i) + "%\')";
//                    added = true;
//                }
//            }
//            filter += ")";
//        }
        return getPlaces(filter, TRASH_ID, context, new TrashFactory());
    }

////    private static String sqlCoordBounds(LatLng min, LatLng max, int category) {
////        return "(" + DBAdapter.getColumnName(category, Place.LAT_DB) + " BETWEEN " +
////                min.latitude + " AND " + max.latitude + ") AND (" +
////                DBAdapter.getColumnName(category, Place.LNG_DB) + " BETWEEN " + min.longitude +
//                " AND " + max.longitude + ")";
//    }

    // Returns null in case of error!
    private <T extends Place> ArrayList<T> getPlaceById(int category, Context context, PlaceFactory<T> f) {
        return getPlaces("WHERE id=\'" + id + "\'", category, context, f);
    }

    @Override
    public void onStartLoading() {
        super.onStartLoading();
        if (isLoading || resultSuccess) {
            Log.d(TAG, "request to database currently running or ended successfully");
        } else {
            forceLoad();
        }
    }

    @Override
    public PlaceResultType loadInBackground() {
        Log.d(TAG, "started loading places from database");
        isLoading = true;
        ArrayList<? extends Place> ans = new ArrayList<>();
        switch (which) {
            case TRASH_ID:
                if (mode == IN_BOUNDS || mode == ALL) {
                    ans = getTrashes(getLatLng(latMinus, lngMinus), getLatLng(latPlus, lngPlus),
                            getContext());
                } else {
                    ans = getPlaceById(which, getContext(), new TrashFactory());
                }
                break;
            case ECOMOBILE_ID:
                if (mode == IN_BOUNDS || mode == ALL) {
                    ans = getEcomobiles(getLatLng(latMinus, lngMinus), getLatLng(latPlus, lngPlus),
                            getContext());
                } else {
                    ans = getPlaceById(which, getContext(), new CafeFactory());
                }
                break;
        }
        if (ans == null) {
            //probably database is not downloaded yet or is damaged (which is unlikely)
            Log.d(TAG, "error while reading from database");
            return new PlaceResultType();
        }
        LatLngBounds bounds = includeAll(ans);
        //TODO move this out of loader and generate CameraUpdate later
        CameraUpdate cu = null;
        if (ans.size() > 0) {
            cu = CameraUpdateFactory.newLatLngBounds(bounds, 10); // WTF is 10?
        }
        Log.d(TAG, "loaded " + ans.size() + " places from database");
        isLoading = false;
        if (mode == ALL) {
            resultSuccess = true;
        }
        return new PlaceResultType(cu, ans, which, mode == BY_ID);
    }
}
