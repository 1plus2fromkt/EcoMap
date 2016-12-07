package com.twofromkt.ecomap.map_activity.bottom_info;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.design.widget.BottomSheetBehavior;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.PlaceTypes.Place;
import com.twofromkt.ecomap.map_activity.MapActivity;

import static com.twofromkt.ecomap.Consts.TRASH_TYPES_NUMBER;

public class BottomInfoView extends LinearLayout {

    BottomSheetBehavior bottomInfo;
    TextView name, information, address;
    View bottomInfoView;
    MapActivity parentActivity;

    Place currPlace;
    ImageView[] trashTypesIcons;
    LinearLayout trashTypesIconsLayout;

    private BottomInfoAdapter adapter;

    public BottomInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.bottom_info, this);
    }

    public void attach(MapActivity parentActivity) {
        this.parentActivity = parentActivity;
        bottomInfoView = findViewById(R.id.bottom_info_scroll_view);
        bottomInfo = BottomSheetBehavior.from(bottomInfoView);
        name = (TextView) findViewById(R.id.bottom_info_name_text);
        address = (TextView) findViewById(R.id.bottom_info_address_text);
        information = (TextView) findViewById(R.id.bottom_info_info_text);
        adapter = new BottomInfoAdapter(this);

        trashTypesIconsLayout = (LinearLayout) findViewById(R.id.bottom_info_trash_icons_layout);
        trashTypesIcons = new ImageView[TRASH_TYPES_NUMBER];
        for (int i = 0; i < TRASH_TYPES_NUMBER; i++) {
            ImageView icon = new ImageView(parentActivity);
            try {
                icon.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.class.getField("trash" + (i + 1) + "selected").getInt(null)));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int marginRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics);
            layoutParams.setMargins(0, 0, marginRight, 0);
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, metrics);
            layoutParams.width = layoutParams.height = size;
            trashTypesIconsLayout.addView(icon, layoutParams);
            icon.setVisibility(VISIBLE);
            trashTypesIcons[i] = icon;
        }

        setListeners();
    }

    private void setListeners() {
        bottomInfo.setBottomSheetCallback(adapter);
    }

    public void addInfo(String name, String category_name) {
        this.name.setText(name);
    }

    public void collapse() {
        bottomInfo.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void hide() {
        bottomInfo.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public boolean isExpanded() {
        return bottomInfo.getState() == BottomSheetBehavior.STATE_EXPANDED;
    }

    public boolean isCollapsed() {
        return bottomInfo.getState() == BottomSheetBehavior.STATE_COLLAPSED;
    }

    public boolean isHidden() {
        return bottomInfo.getState() == BottomSheetBehavior.STATE_HIDDEN;
    }

    //TODO: make different setPlace for Cafe and TrashBox
    public void setPlace(Place place) {
        if (place == null) {
            return;
        }
        this.currPlace = place;
        name.setText(place.name);
        address.setText(place.address);
        information.setText(place.information);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superSaved = super.onSaveInstanceState();
        return new SavedBottomInfo(superSaved, bottomInfo.getState(), currPlace);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedBottomInfo savedState = (SavedBottomInfo) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        bottomInfo.setState(savedState.getState());
        currPlace = savedState.getPlace();
        setPlace(currPlace);
    }

}