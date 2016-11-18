package com.twofromkt.ecomap.map_activity.search_bar;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

class SavedSearchBar extends View.BaseSavedState {

    private int visibility;
    private boolean[] chosen;

    SavedSearchBar(Parcelable superState, int visibility, boolean[] chosen) {
        super(superState);
        this.visibility = visibility;
        this.chosen = chosen;
    }

    int getVisibility() {
        return visibility;
    }

    boolean[] getChosen() {
        return chosen;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(visibility);
        dest.writeBooleanArray(chosen);
    }


}
