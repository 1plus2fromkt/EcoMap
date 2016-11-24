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

    void expand() {
        if (animating)
            return;
//        bar.checkboxes.setVisibility(View.VISIBLE);
        animating = true;
        int dx = bar.checkboxes.getWidth();
        int dy = bar.checkboxes.getHeight();
        float finalRadius = (float) Math.hypot(dx, dy);
        bar.checkboxes.setVisibility(View.VISIBLE);
        Animator animator = ViewAnimationUtils.createCircularReveal(
                bar.checkboxes, bar.checkboxes.getWidth(), 0, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bar.searchBox.setVisibility(View.INVISIBLE);
                animating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animating = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(400);
        animator.start();
    }

    void collapse() {
        if (animating)
            return;
        animating = true;
        int dx = bar.checkboxes.getWidth();
        int dy = bar.checkboxes.getHeight();
        float finalRadius = (float) Math.hypot(dx, dy);
        bar.checkboxes.setVisibility(View.VISIBLE);
        Animator animator = ViewAnimationUtils.createCircularReveal
                (bar.checkboxes, bar.checkboxes.getRight(), 0, finalRadius, 0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                bar.searchBox.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bar.checkboxes.setVisibility(View.INVISIBLE);
                animating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animating = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(400);
        animator.start();
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
