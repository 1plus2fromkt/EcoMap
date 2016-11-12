package com.twofromkt.ecomap.activities;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Pair;

import com.google.android.gms.maps.model.Marker;
import com.twofromkt.ecomap.activities.ListAdapter;
import com.twofromkt.ecomap.activities.MapActivity;
import com.twofromkt.ecomap.db.Place;

import java.util.ArrayList;
import java.util.List;

import static com.twofromkt.ecomap.activities.MapActivity.CATEGORIES_N;

class ListViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> tabs = new ArrayList<>();
    final static String[] tabNames = new String[]{"Мусор", "Кафе", "Прочее"};
    private final ArrayList<ArrayList<Pair<Marker, ? extends Place>> > a;
    Context context;
    public int r = 0;

    public ListViewPagerAdapter(FragmentManager manager, ArrayList <ArrayList<Pair<Marker, ? extends Place>>> a, Context context) {
        super(manager);
        this.a = a;
        this.context = context;
    }


    @Override
    public Fragment getItem(int position) {
        return new OneList().setAdapter(new ListAdapter(a.get(getPos(position))));
    }

    @Override
    public int getCount() {
//        int ans = 0;
//        for (int i = 0; i < CATEGORIES_N; i++)
//            if (a.get(i).size() > 0)
//                ans++;
//        return ans;
        return tabNames.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames[getPos(position)];
    }

    public int getPos(int pos) {
//        int ans = 0;
//        pos++;
//        for (int i = 0; i < CATEGORIES_N; i++) {
//            if (a.get(i).size() > 0)
//                pos--;
//            ans++;
//            if (pos == 0)
//                return ans;
//        }
//        return ans;
        return pos;
    }

}