package com.twofromkt.ecomap.map_activity.bottom_sheet;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Pair;

import com.twofromkt.ecomap.place_types.Place;
import com.twofromkt.ecomap.map_activity.MapActivity;
import com.twofromkt.ecomap.map_activity.map.MapClusterItem;
import com.twofromkt.ecomap.util.Util;

import java.util.ArrayList;

import static com.twofromkt.ecomap.Consts.CATEGORIES_NUMBER;

class ListViewPagerAdapter extends FragmentPagerAdapter {

    OneList[] tabs;
    final static String[] tabNames = new String[]{"Мусор", "Кафе", "Прочее"};

    ListViewPagerAdapter(FragmentManager manager,
                         ArrayList<ArrayList<Util.PlaceWithCoord>> a,
                         MapActivity act) {
        super(manager);
        int CATEGORIES_NUMBER = 3;
        tabs = new OneList[CATEGORIES_NUMBER];
        for (int i = 0; i < CATEGORIES_NUMBER; i++) {
//            tabs[i] = new OneList();
            tabs[i].adapter = new ListAdapter(a.get(i), act);
        }
    }

//    void notifyUpdate() {
//        for (int i = 0; i < CATEGORIES_NUMBER; i++) {
//            tabs[i].adapter.notifyDataSetChanged();
//        }
//    }

    /**
     * Updates the list in the tab number index with newData.
     * Note that newData can be passed by reference because it is
     * being copied inside ListAdapter
     * @param index index of tab we need to update
     * @param newData New data to update with
     */
    void updateList(int index, ArrayList<Util.PlaceWithCoord> newData) {
        tabs[index].adapter.updateData(newData);
    }

    @Override
    public Fragment getItem(int position) {
//        return tabs[position];
        return null;
    }

    @Override
    public int getCount() {
//        return tabNames.length;
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames[position];
    }

}