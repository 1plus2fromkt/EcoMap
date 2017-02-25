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
import static com.twofromkt.ecomap.Consts.TRASH_ID;

class ListViewPagerAdapter extends FragmentPagerAdapter {

    OneList[] tabs;
    private static String[] trashTabNames = new String[]{"Мусор", "Расписание"}; // in future we can change it so it is not final
    static String[] tabNames;

    ListViewPagerAdapter(FragmentManager manager,
                         ArrayList<ArrayList<Util.PlaceWithCoord>> a,
                         MapActivity act) {
        super(manager);

//        if (act.typePanel != null && act.typePanel.isChosen(TRASH_ID)) {
        //TODO ^ uncomment this if
            tabNames = trashTabNames;

            tabs = new OneList[tabNames.length];
            for (int i = 0; i < tabNames.length; i++) {
                tabs[i] = new OneList();
            }
            // NOT GOOD CODE
            tabs[0].adapter = new ListAdapter(a.get(0), act);
        // TODO Uncomment the string below, with added Ecomobiles
            // tabs[1].adapter = new TimeTableAdapter(a.get(1), act);

//        }
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
        ((ListAdapter)tabs[index].adapter).updateData(newData);
    }

    @Override
    public Fragment getItem(int position) {
        return tabs[position];
    }

    @Override
    public int getCount() {
        return CATEGORIES_NUMBER;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames[position];
    }

}