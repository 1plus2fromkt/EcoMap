package com.twofromkt.ecomap.map_activity.bottom_info;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;

class BottomInfoAdapter extends BottomSheetBehavior.BottomSheetCallback {

    private BottomInfoView bottomInfo;

    BottomInfoAdapter(BottomInfoView bottomInfo) {
        this.bottomInfo = bottomInfo;
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {
        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
            bottomInfo.hide();
            bottomInfo.parentActivity.bottomSheet.collapse();
        }
    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

    }
}
