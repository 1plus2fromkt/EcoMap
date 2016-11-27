package com.twofromkt.ecomap.map_activity.choose_type_panel;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

class SavedChooseTypePanel extends View.BaseSavedState {

    private boolean showing;
    private boolean[] chosenTypes;
    private float panelOffset;

    SavedChooseTypePanel(Parcelable superState, boolean showing, boolean[] chosenTypes,
                         float panelOffset) {
        super(superState);
        this.showing = showing;
        this.chosenTypes = chosenTypes;
        this.panelOffset = panelOffset;
    }

    boolean getShowing() {
        return showing;
    }

    boolean[] getChosenTypes() {
        return chosenTypes;
    }

    float getPanelOffset() {
        return panelOffset;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(showing ? 1 : 0);
        dest.writeBooleanArray(chosenTypes);
        dest.writeFloat(panelOffset);
    }

}