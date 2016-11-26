package com.twofromkt.ecomap.map_activity.bottom_info;

import android.content.Context;
import android.os.Parcelable;
import android.support.design.widget.BottomSheetBehavior;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.db.Place;
import com.twofromkt.ecomap.map_activity.MapActivity;

public class BottomInfoView extends LinearLayout {

    BottomSheetBehavior bottomInfo;
    TextView name, categoryName, information, location;
    View bottomInfoView;
    MapActivity parentActivity;

    Place currPlace;

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
        name = (TextView) findViewById(R.id.name_text);
        categoryName = (TextView) findViewById(R.id.category_text);
        information = (TextView) findViewById(R.id.information_text);
        location = (TextView) findViewById(R.id.location_text);
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

//    public void setName(String nameText) {
//        name.setText(nameText);
//    }

//    public void setCategoryName(String categoryNameText) {
//        categoryName.setText(categoryNameText);
//    }

    public boolean isOpened() {
        return bottomInfo.getState() != BottomSheetBehavior.STATE_HIDDEN;
    }

    //TODO: make different setPlace for Cafe and TrashBox
    public void setPlace(Place place) {
        if (place == null) {
            return;
        }
        this.currPlace = place;
        name.setText(place.name);
        String currCategoryName;
        switch (place.categoryNumber) {
            case Place.CAFE:
                currCategoryName = "Cafe";
                break;
            case Place.TRASHBOX:
                currCategoryName = "Trashbox";
                break;
            case Place.OTHER:
                currCategoryName = "Other";
                break;
            default:
                currCategoryName = "Unknown";
        }
        categoryName.setText(currCategoryName);
        information.setText(place.information);
        location.setText(place.location.toString());
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
