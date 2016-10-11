package com.twofromkt.ecomap.db;

import android.content.Context;

import com.android.internal.util.Predicate;
import com.google.android.gms.maps.model.LatLng;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static com.twofromkt.ecomap.Util.*;

public class GetPlaces {
    private static final String[] FILE_NAMES = new String[]{"cafes", "trashes"};
    public static final int CAFE = 0, TRASH = 1;
    public static void putObject(Place p, int category, Context cont) {
        try {
            File f = new File(cont.getFilesDir(), FILE_NAMES[category]);
            boolean q = f.exists() && !f.isDirectory(); // might be crap
            FileOutputStream out = new FileOutputStream(f, true);
            ObjectOutputStream outO;
            if (!q)
                outO = new ObjectOutputStream(out);
            else
                outO = new AppendingObjectOutputStream(out);
            outO.writeObject(p);
            outO.flush();
            outO.close();
            out.close();
        } catch (IOException e) { //TODO: do something good
            e.printStackTrace();
        }
    }

    private static <T extends Place> ArrayList<T> getPlaces(Predicate<T> pr, int category, Context context) {
        ArrayList<T> ans = new ArrayList<>();
        try {
            File f = new File(context.getFilesDir(), FILE_NAMES[category]);
            FileInputStream in = new FileInputStream(f);
            ObjectInputStream inO = new ObjectInputStream(in);
            T x;
            try {
                while (true) {
                    x = (T) inO.readObject();
                    if (pr.apply(x))
                        ans.add(x);
                }
            } catch (EOFException ignored) {}
            inO.close();
            in.close();
        } catch (IOException | ClassNotFoundException e) { //TODO: do something good
            e.printStackTrace();
        }
        return ans;
    }

    public static ArrayList<Cafe> getCafes(final LatLng x, final double radii, Context context) {

        return getPlaces(new Predicate<Cafe>() {
            @Override
            public boolean apply(Cafe o) {
                return distanceLatLng(x, fromPair(o.location)) < radii;
            }
        }, CAFE, context);
    }

    public static ArrayList<TrashBox> getTrashes(final LatLng x, final double radii,
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
                return distanceLatLng(x, fromPair(o.location)) < radii && finalS.size() > 0;
            }
        }, TRASH, context);
    }



}
