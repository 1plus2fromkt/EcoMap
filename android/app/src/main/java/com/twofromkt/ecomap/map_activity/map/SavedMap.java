package com.twofromkt.ecomap.map_activity.map;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

class SavedMap extends View.BaseSavedState {

    private boolean locationButtonUp;
    private boolean hasCustomLocation;
    private boolean placesLoaded;

    SavedMap(Parcelable superState, boolean locationButtonUp, boolean hasCustomLocation,
             boolean placesLoaded) {
        super(superState);
        this.locationButtonUp = locationButtonUp;
        this.hasCustomLocation = hasCustomLocation;
        this.placesLoaded = placesLoaded;
    }

    boolean getLocationButtonUp() {
        return locationButtonUp;
    }

    boolean hasCustomLocation() {
        return hasCustomLocation;
    }

    boolean placesLoaded() {
        return placesLoaded;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

}