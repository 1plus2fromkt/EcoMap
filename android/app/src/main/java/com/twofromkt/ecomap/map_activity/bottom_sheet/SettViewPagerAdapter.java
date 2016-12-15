package com.twofromkt.ecomap.map_activity.bottom_sheet;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.twofromkt.ecomap.Consts;
import com.twofromkt.ecomap.place_types.Place;
import com.twofromkt.ecomap.map_activity.MapActivity;

class SettViewPagerAdapter extends FragmentPagerAdapter {

    Sett.TrashSett trashSett;
    Sett.CafeSett cafeSett;
    Sett.OtherSett otherSett;

    SettViewPagerAdapter(FragmentManager fm, MapActivity a) {
        super(fm);
        trashSett = new Sett.TrashSett();
        trashSett.setMapActivity(a);
        cafeSett = new Sett.CafeSett();
        cafeSett.setMapActivity(a);
        otherSett = new Sett.OtherSett();
        otherSett.setMapActivity(a);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case Place.TRASHBOX:
                return trashSett;
            case Place.CAFE:
                return cafeSett;
            case Place.OTHER:
                return otherSett;

        }
        return null;
    }

    @Override
    public int getCount() {
        return Consts.CATEGORIES_NUMBER;
    }

    @Override
    public CharSequence getPageTitle(int pos) {
        return ListViewPagerAdapter.tabNames[pos];
    }

}
