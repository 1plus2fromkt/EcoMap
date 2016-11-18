package com.twofromkt.ecomap.map_activity.search_bar;

import android.location.Address;
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
//            Address address = findNearestAddress(
//                    bar.searchBar.getText().toString(),
//                    bar.parentActivity,
//                    getLocation(bar.parentActivity.map.locationManager, bar.parentActivity.map.criteria));
//            bar.parentActivity.map.addMarker(new TrashBox(
//                    "found place",
//                    new LatLng(address.getLatitude(), address.getLongitude()),
//                    "info", null, "sosi", new HashSet<TrashBox.Category>()), TRASH_NUM);
            MapActivityUtil.closeKeyboard(bar.parentActivity);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < CATEGORIES_N; i++) {
            if (v == bar.checkboxButtons[i]) {
                bar.util.setChosen(i, !bar.chosenCheck[i], true);
                return;
            }
        }
        if (v == bar.openMenuButton) {
//            bar.parentActivity.drawerLayout.openDrawer(act.nv);
            return;
        }
        if (v == bar.showChecks) {
            if (!bar.util.animating) {
                v.setRotation(bar.showChecks.getRotation() + 180);
            }

            if (bar.checkboxes.getVisibility() == View.VISIBLE) {
                bar.util.collapse();
            } else {
                bar.util.expand();
            }
//            return;
        }
    }
}
