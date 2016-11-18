package com.twofromkt.ecomap.map_activity.bottom_sheet_view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.twofromkt.ecomap.map_activity.MapActivity;


public class SettViewPagerAdapter extends FragmentPagerAdapter {

    TrashSett trashSett;
    CafeSett cafeSett;
    OtherSett otherSett;

    SettViewPagerAdapter(FragmentManager fm, MapActivity a) {
        super(fm);
        trashSett = new TrashSett();
        trashSett.setMapActivity(a);
        cafeSett = new CafeSett();
        cafeSett.setMapActivity(a);
        otherSett = new OtherSett();
        otherSett.setMapActivity(a);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case MapActivity.TRASH_NUM:
                return trashSett;
            case MapActivity.CAFE_NUM:
                return cafeSett;
            case MapActivity.OTHER_NUM:
                return otherSett;

        }
        return null;
    }

    @Override
    public int getCount() {
        return MapActivity.CATEGORIES_N;
    }

    @Override
    public CharSequence getPageTitle(int pos) {
        return ListViewPagerAdapter.tabNames[pos];
    }




}
