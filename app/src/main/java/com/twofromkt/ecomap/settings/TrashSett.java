package com.twofromkt.ecomap.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.activities.MapActivity;

import static com.twofromkt.ecomap.activities.MapActivity.TRASH_NUM;

public class TrashSett extends android.support.v4.app.Fragment {

    final MapActivity mapActivity;

    public TrashSett(MapActivity mapActivity) {
        this.mapActivity = mapActivity;
        trashCategoryButtons = new Button[TRASH_N];
        chosen = new boolean[TRASH_N];
    }

    public boolean[] chosen;
    public Button[] trashCategoryButtons;
    public static final int TRASH_N = 3;
    final static float[] ALPHAS = new float[]{(float) 0.6, 1};

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
                    if (mapActivity.chosenCheck[TRASH_NUM]) {
                        mapActivity.searchTrashes();
                    }
                }
            });
        }
        return view;
    }
}