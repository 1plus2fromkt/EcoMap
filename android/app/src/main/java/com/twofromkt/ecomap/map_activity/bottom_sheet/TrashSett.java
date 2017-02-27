package com.twofromkt.ecomap.map_activity.bottom_sheet;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.map_activity.MapActivity;
import com.twofromkt.ecomap.place_types.TrashBox;

import java.util.Arrays;

import static com.twofromkt.ecomap.Consts.TRASH_TYPES_NUMBER;

public class TrashSett extends Sett {

    boolean[] chosen;
    ImageButton[] trashCategoryButtons;

    public TrashSett(Context context, AttributeSet attrs) {
        super(context, attrs);
        trashCategoryButtons = new ImageButton[TRASH_TYPES_NUMBER];
        chosen = new boolean[TRASH_TYPES_NUMBER];
        Arrays.fill(chosen, true);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.fragment_trash_sett, this);
    }

    public void attach(MapActivity parentActivity) {
        mapActivity = parentActivity;
        for (int i = 0; i < TRASH_TYPES_NUMBER; i++) {
            try {
                RelativeLayout checkbox = (RelativeLayout) findViewById(
                        R.id.class.getField("trash_checkbox" + (i + 1)).getInt(null));
                ImageButton button = (ImageButton) checkbox.findViewById(R.id.trash_checkbox);

                button.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.class.getField("trash" + (i + 1) + "selected").getInt(null)));
                trashCategoryButtons[i] = button;
                TextView name = (TextView) checkbox.findViewById(R.id.trash_name);
                String text = TrashBox.Category.names[i];
                if (text.length() > 9) { //TODO replace magic constant somehow
                    //TODO do something clever here to fit text
                    if (text.equals("Бытовая техника")) {
                        text = "Техника";
                    }
                }
                name.setText(text);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < TRASH_TYPES_NUMBER; i++) {
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
                    mapActivity.map.showTrashMarkers();
                }
            });
        }
    }
}