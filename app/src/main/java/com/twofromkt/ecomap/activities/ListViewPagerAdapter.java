package com.twofromkt.ecomap.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Pair;

import com.google.android.gms.maps.model.Marker;
import com.twofromkt.ecomap.db.Place;

import java.util.ArrayList;

import static com.twofromkt.ecomap.activities.MapActivity.CATEGORIES_N;

class ListViewPagerAdapter extends FragmentPagerAdapter {
    OneList[] tabs;
    final static String[] tabNames = new String[]{"Мусор", "Кафе", "Прочее"};
    public int r = 0;

    public ListViewPagerAdapter(FragmentManager manager,
                                ArrayList <ArrayList<Pair<Marker, ? extends Place>>> a,
                                MapActivity act) {
        super(manager);
        tabs = new OneList[CATEGORIES_N];
        for (int i = 0; i < CATEGORIES_N; i++) {
            tabs[i] = new OneList();
            tabs[i].a = new ListAdapter(a.get(i), act);
        }
    }

    public void notifyUpdate() {
        for (int i = 0; i < CATEGORIES_N; i++)
            tabs[i].a.notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return tabs[position];
    }

    @Override
    public int getCount() {
        return tabNames.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames[position];
    }


}