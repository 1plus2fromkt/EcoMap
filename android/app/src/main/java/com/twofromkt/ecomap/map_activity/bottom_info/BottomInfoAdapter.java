package com.twofromkt.ecomap.map_activity.bottom_info;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.view.View;

import com.twofromkt.ecomap.R;

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
        } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomInfo.bottomInfoView.scrollTo(0, 0);
        }
    }

    @Override
    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

    }
}
