package com.twofromkt.ecomap.map_activity.bottom_sheet;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

class SavedBottomSheet extends View.BaseSavedState {

    private int state;

    SavedBottomSheet(Parcelable superState, int state) {
        super(superState);
        this.state = state;
    }

    public int getState() {
        return state;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(state);
    }

}
