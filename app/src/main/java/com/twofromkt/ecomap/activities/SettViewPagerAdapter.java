package com.twofromkt.ecomap.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.twofromkt.ecomap.settings.CafeSett;
import com.twofromkt.ecomap.settings.OtherSett;
import com.twofromkt.ecomap.settings.TrashSett;


public class SettViewPagerAdapter extends FragmentPagerAdapter{

    TrashSett trashSett;
    CafeSett cafeSett;
    OtherSett otherSett;

    SettViewPagerAdapter(FragmentManager fm, MapActivity a) {
        super(fm);
        trashSett = new TrashSett(a);
        cafeSett = new CafeSett(a);
        otherSett = new OtherSett(a);
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
