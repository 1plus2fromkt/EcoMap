package com.twofromkt.ecomap.map_activity.bottom_sheet;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.map_activity.MapActivity;

import java.util.Arrays;

public abstract class Sett extends android.support.v4.app.Fragment {
    MapActivity mapActivity;

    public void setMapActivity(MapActivity act) {
        this.mapActivity = act;
    }

    public static class CafeSett extends Sett {

        public CafeSett() {
        }

        @Override
        public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstance) {
            return li.inflate(R.layout.cafe_sett_fragment, null);
        }
    }

    public static class TrashSett extends Sett {

        public TrashSett() {
            trashCategoryButtons = new ImageButton[TRASH_N];
            chosen = new boolean[TRASH_N];
            Arrays.fill(chosen, true);
        }

        boolean[] chosen;
        ImageButton[] trashCategoryButtons;
        static final int TRASH_N = 4;
        static final int[] buttonIds = {R.id.clothes_trash_checkbox, R.id.paper_trash_checkbox,
                            R.id.plastic_trash_checkbox, R.id.metal_trash_checkbox};

        @Override
        public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstance) {
            View view = li.inflate(R.layout.categories_fragment, null);
            for (int i = 0; i < TRASH_N; i++) {
                trashCategoryButtons[i] = (ImageButton) view.findViewById(buttonIds[i]);
                final int fi = i;
                trashCategoryButtons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chosen[fi] = !chosen[fi];
                        int iconId = chosen[fi] ? R.mipmap.selected_icon : R.mipmap.unselected_icon;
                        trashCategoryButtons[fi].setImageBitmap(
                                BitmapFactory.decodeResource(getResources(), iconId));
//                    if (mapActivity.chosenCheck[TRASH_NUM]) {
//                        mapActivity.searchTrashes();
//                    }
                    }
                });
            }
            return view;
        }
    }

    public static class OtherSett extends Sett {
        public OtherSett() {
        }

        @Override
        public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstance) {
            return li.inflate(R.layout.other_sett_fragment, null);
        }
    }
}
