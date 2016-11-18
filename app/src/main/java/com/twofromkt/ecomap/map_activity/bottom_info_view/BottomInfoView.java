package com.twofromkt.ecomap.map_activity.bottom_info_view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.map_activity.MapActivity;
import com.twofromkt.ecomap.map_activity.MapActivityUtil;

public class BottomInfoView extends LinearLayout {

    BottomSheetBehavior bottomInfo;
    TextView name, category_name;
    View bottomInfoView;
    MapActivity parentActivity;

    public BottomInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.bottom_info, this);
    }

    public void attach(MapActivity parentActivity) {
        this.parentActivity = parentActivity;
        bottomInfoView = findViewById(R.id.bottom_sheet);
        bottomInfo = BottomSheetBehavior.from(bottomInfoView);

        setListeners();
    }

    private void setListeners() {
        bottomInfo.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    MapActivityUtil.hideBottomInfo(parentActivity);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    public void addInfo(String name, String category_name) {
        this.name.setText(name);
        this.category_name.setText(category_name);
    }

}
