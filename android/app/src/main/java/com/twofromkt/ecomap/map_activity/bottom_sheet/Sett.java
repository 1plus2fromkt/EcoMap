package com.twofromkt.ecomap.map_activity.bottom_sheet;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.map_activity.MapActivity;

import java.util.Arrays;

import static com.twofromkt.ecomap.Consts.TRASH_TYPES_NUMBER;

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
            return li.inflate(R.layout.fragment_cafe_sett, null);
        }
    }

    public static class TrashSett extends Sett {

        boolean[] chosen;
        ImageButton[] trashCategoryButtons;
        int[] buttonIds;

        public TrashSett() {
            trashCategoryButtons = new ImageButton[TRASH_TYPES_NUMBER];
            chosen = new boolean[TRASH_TYPES_NUMBER];
            Arrays.fill(chosen, true);
        }

        @Override
        public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstance) {
            buttonIds = new int[TRASH_TYPES_NUMBER];
            for (int i = 0; i < TRASH_TYPES_NUMBER; i++) {
                try {
                    buttonIds[i] = R.id.class.getField("trash_checkbox" + (i + 1)).getInt(null);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
            View view = li.inflate(R.layout.fragment_trash_sett, null);
            for (int i = 0; i < 4; i++) { //TODO 4 -> TRASH_TYPES_NUMBER
                trashCategoryButtons[i] = (ImageButton) view.findViewById(buttonIds[i]);
                final int fi = i;
                trashCategoryButtons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chosen[fi] = !chosen[fi];
                        String iconName = "trash" + (fi + 1) + (chosen[fi] ? "selected" : "");
                        int iconId;
                        try {
                            iconId = R.mipmap.class.getField(iconName).getInt(null);
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            e.printStackTrace();
                            Log.d("trash_type_buttons", "icon not found");
                            return;
                        }
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
            return li.inflate(R.layout.fragment_other_sett, null);
        }
    }
}
