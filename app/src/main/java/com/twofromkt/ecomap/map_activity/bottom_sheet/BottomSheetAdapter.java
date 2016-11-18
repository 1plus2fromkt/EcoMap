package com.twofromkt.ecomap.map_activity.bottom_sheet;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.view.MotionEvent;
import android.view.View;

import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;

class BottomSheetAdapter extends ViewPagerBottomSheetBehavior.BottomSheetCallback
        implements View.OnTouchListener {

    private BottomSheetView sheet;
    private boolean isCategory;

    BottomSheetAdapter(BottomSheetView sheet) {
        this.sheet = sheet;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (sheet.bottomList.getState() == BottomSheetBehavior.STATE_COLLAPSED &&
                sheet.listLayout.getVisibility() == View.INVISIBLE &&
                sheet.categoriesLayout.getVisibility() == View.INVISIBLE) {
            isCategory = (event.getAxisValue(MotionEvent.AXIS_X) > v.getWidth() / 2);
            sheet.categoriesLayout.setAlpha(0);
            sheet.listLayout.setAlpha(0);
            sheet.listLayout.setVisibility(isCategory ? View.INVISIBLE : View.VISIBLE);
            sheet.categoriesLayout.setVisibility(isCategory ? View.VISIBLE : View.INVISIBLE);
            return true;
        }
        return false;
    }

    @Override
    public void onStateChanged(@NonNull View view, int newState) {
        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            sheet.listLayout.setVisibility(View.INVISIBLE);
            sheet.categoriesLayout.setVisibility(View.INVISIBLE);
        }
        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
            sheet.listViewPager.requestFocus();
        }
    }

    @Override
    public void onSlide(@NonNull View view, float slideOffset) {
        if (slideOffset < 1) {
            sheet.collapsedPart.setVisibility(View.VISIBLE);
            sheet.collapsedPart.setAlpha(1 - slideOffset);
            if (sheet.parentActivity.adapter.isCategory) {
                sheet.categoriesLayout.setAlpha(slideOffset);
                sheet.listLayout.setVisibility(View.INVISIBLE);
            } else {
                sheet.listLayout.setAlpha(slideOffset);
                sheet.categoriesLayout.setVisibility(View.INVISIBLE);
            }
        }
        if (slideOffset == 1) {
            sheet.collapsedPart.setVisibility(View.INVISIBLE);
        }
    }
}
