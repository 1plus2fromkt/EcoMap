package com.twofromkt.ecomap.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.twofromkt.ecomap.R;

import static com.twofromkt.ecomap.activities.CategoriesActivity.TRASH_N;
import static com.twofromkt.ecomap.activities.MapActivityUtil.ALPHAS;

public class SettViewPagerAdapter extends FragmentPagerAdapter{

    static boolean[] chosen;
    static Button[] trashCategoryButtons;

    SettViewPagerAdapter(FragmentManager fm) {
        super(fm);
        chosen = new boolean[TRASH_N];
        trashCategoryButtons = new Button[TRASH_N];
    }

    static void setChosen(boolean[] chosen1) {
        chosen = chosen1;
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
            View view = li.inflate(R.layout.categories_fragment, null);
            for (int i = 0; i < TRASH_N; i++) {
                try {
                    trashCategoryButtons[i] = (Button) view.findViewById((Integer) R.id.class.getField("trash" + (i + 1)).get(null));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                trashCategoryButtons[i].setAlpha(ALPHAS[chosen[i] ? 1 : 0]);
                final int fi = i;
                trashCategoryButtons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chosen[fi] = !chosen[fi];
                        trashCategoryButtons[fi].setAlpha(ALPHAS[chosen[fi] ? 1 : 0]);
                    }
                });
            }
            return view;
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
