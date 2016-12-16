package com.twofromkt.ecomap.map_activity.search_bar;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

class SavedSearchBar extends View.BaseSavedState {

    private boolean progressBarShown;

    SavedSearchBar(Parcelable superState, boolean progressBarShown) {
        super(superState);
        this.progressBarShown = progressBarShown;
    }

    boolean isProgressBarShown() {
        return progressBarShown;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeBooleanArray(new boolean[]{progressBarShown});
    }

}