package com.twofromkt.ecomap.map_activity.search_bar_view;

import android.location.Address;
import android.location.Location;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.twofromkt.ecomap.db.TrashBox;
import com.twofromkt.ecomap.map_activity.MapActivityUtil;

import java.util.HashSet;

import static com.twofromkt.ecomap.map_activity.MapActivity.CAFE_NUM;
import static com.twofromkt.ecomap.map_activity.MapActivity.CATEGORIES_N;
import static com.twofromkt.ecomap.map_activity.MapActivity.TRASH_NUM;
import static com.twofromkt.ecomap.util.LocationUtil.findNearestAddress;
import static com.twofromkt.ecomap.util.LocationUtil.getLocation;

class SearchBarAdapter implements EditText.OnEditorActionListener, View.OnClickListener {

    private SearchBarView bar;

    SearchBarAdapter(SearchBarView bar) {
        this.bar = bar;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            Address address = findNearestAddress(
                    bar.searchBar.getText().toString(),
                    bar.parentActivity,
                    getLocation(bar.parentActivity.locationManager, bar.parentActivity.criteria));
            bar.parentActivity.util.addMarker(bar.parentActivity.map.getMap(), new TrashBox(
                    "found place",
                    new LatLng(address.getLatitude(), address.getLongitude()),
                    "info", null, "sosi", new HashSet<TrashBox.Category>()), TRASH_NUM);
            MapActivityUtil.closeKeyboard(bar.parentActivity);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < CATEGORIES_N; i++) {
            if (v == bar.checkboxButtons[i]) {
                if (bar.chosenCheck[i]) {
                    bar.chosenCheck[i] = false;
                    bar.checkboxButtons[i].setAlpha((float) 0.5);
                    bar.parentActivity.util.clearMarkers(i);
                } else {
                    bar.chosenCheck[i] = true;
                    bar.checkboxButtons[i].setAlpha((float) 1);
                    if (i == TRASH_NUM) {
                        bar.parentActivity.map.searchNearTrashes();
                    } else if (i == CAFE_NUM) {
                        bar.parentActivity.map.searchNearCafe();
                    }
                    bar.parentActivity.listViewPager.setCurrentItem(i, true);
                }
                return;
            }
        }
        if (v == bar.openMenuButton) {
//            bar.parentActivity.drawerLayout.openDrawer(act.nv);
            return;
        }
        if (v == bar.showChecks) {
            if (!MapActivityUtil.isAnimating) {
                v.setRotation(bar.showChecks.getRotation() + 180);
            }

            if (bar.checkboxes.getVisibility() == View.VISIBLE) {
                MapActivityUtil.collapse(bar.checkboxes, bar.searchBox);
            } else {
                MapActivityUtil.expand(bar.checkboxes, bar.searchBox);
            }
            return;
        }
    }
}
