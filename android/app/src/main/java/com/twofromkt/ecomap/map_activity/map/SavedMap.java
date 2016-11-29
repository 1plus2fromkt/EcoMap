package com.twofromkt.ecomap.map_activity.map;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.twofromkt.ecomap.PlaceTypes.Place;

class SavedMap extends View.BaseSavedState {

    private boolean locationButtonUp;
    private boolean hasCustomLocation;

    SavedMap(Parcelable superState, boolean locationButtonUp, boolean hasCustomLocation) {
        super(superState);
        this.locationButtonUp = locationButtonUp;
        this.hasCustomLocation = hasCustomLocation;
    }

    boolean getLocationButtonUp() {
        return locationButtonUp;
    }

    boolean hasCustomLocation() {
        return hasCustomLocation;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

}