package com.twofromkt.ecomap.map_activity.bottom_info;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.twofromkt.ecomap.db.Place;

class SavedBottomInfo extends View.BaseSavedState {

    private int state;
    private Place place;

    SavedBottomInfo(Parcelable superState, int state, Place place) {
        super(superState);
        this.state = state;
        this.place = place;
    }

    int getState() {
        return state;
    }

    Place getPlace() {
        return place;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(state);
        dest.writeSerializable(place);
    }

}
