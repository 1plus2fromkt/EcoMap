package com.twofromkt.ecomap.map_activity.bottom_info;

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
    TextView name, categoryName;
    View bottomInfoView;
    MapActivity parentActivity;

    private BottomInfoAdapter adapter;

    public BottomInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.bottom_info, this);
    }

    public void attach(MapActivity parentActivity) {
        this.parentActivity = parentActivity;
        bottomInfoView = findViewById(R.id.bottom_sheet);
        bottomInfo = BottomSheetBehavior.from(bottomInfoView);
        name = (TextView) findViewById(R.id.name_text);
        categoryName = (TextView) findViewById(R.id.category_text);
        adapter = new BottomInfoAdapter(this);
        setListeners();
    }

    private void setListeners() {
        bottomInfo.setBottomSheetCallback(adapter);
    }

    public void addInfo(String name, String category_name) {
        this.name.setText(name);
        this.categoryName.setText(category_name);
    }

    public void hide() {
//        act.navigationButton.setVisibility(View.INVISIBLE);
//        act.locationButton.setVisibility(View.VISIBLE);
        bottomInfo.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void show(boolean showSheet) {
//        act.navigationButton.setVisibility(View.VISIBLE);
//        act.locationButton.setVisibility(View.INVISIBLE);
        if (showSheet) {
            bottomInfo.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public boolean isOpened() {
        return bottomInfo.getState() != BottomSheetBehavior.STATE_HIDDEN;
    }
}
