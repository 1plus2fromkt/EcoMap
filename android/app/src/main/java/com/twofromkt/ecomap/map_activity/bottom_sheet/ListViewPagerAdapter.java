package com.twofromkt.ecomap.map_activity.bottom_sheet;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Pair;

import com.twofromkt.ecomap.place_types.Place;
import com.twofromkt.ecomap.map_activity.MapActivity;
import com.twofromkt.ecomap.map_activity.map.MapClusterItem;

import java.util.ArrayList;

import static com.twofromkt.ecomap.Consts.CATEGORIES_NUMBER;

class ListViewPagerAdapter extends FragmentPagerAdapter {

    OneList[] tabs;
    final static String[] tabNames = new String[]{"Мусор", "Кафе", "Прочее"};

    ListViewPagerAdapter(FragmentManager manager,
                                ArrayList<ArrayList<Pair<MapClusterItem, ? extends Place>>> a,
                                MapActivity act) {
        super(manager);
        tabs = new OneList[CATEGORIES_NUMBER];
        for (int i = 0; i < CATEGORIES_NUMBER; i++) {
            tabs[i] = new OneList();
            tabs[i].adapter = new ListAdapter(a.get(i), act);
        }
    }

    void notifyUpdate() {
        for (int i = 0; i < CATEGORIES_NUMBER; i++) {
            tabs[i].adapter.notifyDataSetChanged();
        }
    }

//    void resetTrashList(MapActivity act, ArrayList<Pair<MapClusterItem, ? extends Place>> data) {
//        tabs[0].adapter = new ListAdapter(data, act);
//    }

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