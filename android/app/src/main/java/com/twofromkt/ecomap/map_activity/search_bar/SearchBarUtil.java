package com.twofromkt.ecomap.map_activity.search_bar;

import android.animation.Animator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.twofromkt.ecomap.db.Place;

import io.codetail.animation.ViewAnimationUtils;

class SearchBarUtil {

    private SearchBarView bar;
    boolean animating;

    SearchBarUtil(SearchBarView bar) {
        this.bar = bar;
    }

    /**
     * Set a checkbox state. In case to just change the state without interacting with
     * other components call with activateMap = false
     *
     * @param index index of checkbox to be chosen
     * @param state true if checkbox should be chosen, false otherwise
     * @param activateMap true if the method should change map to show new objects
     */
    void setChosen(int index, boolean state, boolean activateMap) {
//        bar.chosenCheck[index] = state;
//        bar.checkboxButtons[index].setAlpha((float) (state ? 1 : 0.5));
        if (!activateMap) {
            return;
        }
        if (state) {
            if (index == Place.TRASHBOX) {
                bar.parentActivity.map.searchNearTrashes();
            } else if (index == Place.CAFE) {
                bar.parentActivity.map.searchNearCafe();
            }
            bar.parentActivity.bottomSheet.focusOnTab(index);
        } else {
            bar.parentActivity.map.clearMarkers(index);
        }
    }
}
