package com.twofromkt.ecomap.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twofromkt.ecomap.R;

public class SettViewPagerAdapter extends FragmentPagerAdapter{
    SettViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case MapActivity.TRASH_NUM:
                return new TrashSett();
            case MapActivity.CAFE_NUM:
                return new CafeSett();
            case MapActivity.OTHER_NUM:
                return new OtherSett();

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

    public static class TrashSett extends android.support.v4.app.Fragment {
        @Override
        public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstance) {
            return li.inflate(R.layout.categories_fragment, null);
        }
    }
    public static class CafeSett extends android.support.v4.app.Fragment {
        @Override
        public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstance) {
            return li.inflate(R.layout.cafe_sett_fragment, null);
        }
    }
    public static class OtherSett extends android.support.v4.app.Fragment {
        @Override
        public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstance) {
            return li.inflate(R.layout.other_sett_fragment, null);
        }
    }

}
