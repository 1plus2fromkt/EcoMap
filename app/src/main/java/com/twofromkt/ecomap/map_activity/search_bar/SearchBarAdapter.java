package com.twofromkt.ecomap.map_activity.search_bar;

import android.location.Address;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.twofromkt.ecomap.db.Place;
import com.twofromkt.ecomap.db.TrashBox;
import com.twofromkt.ecomap.map_activity.MapActivityUtil;

import java.util.HashSet;

import static com.twofromkt.ecomap.map_activity.MapActivity.CATEGORIES_N;

class SearchBarAdapter implements EditText.OnEditorActionListener, View.OnClickListener {

    private SearchBarView bar;

    SearchBarAdapter(SearchBarView bar) {
        this.bar = bar;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            Address address = bar.parentActivity.map.findNearestAddress(
                    bar.searchBar.getText().toString());
            if (address == null) {
                Toast.makeText(bar.parentActivity, "Address not found", Toast.LENGTH_LONG).show();
                return false;
            }
            bar.parentActivity.map.addMarker(new TrashBox(
                    "Place that I found",
                    new LatLng(address.getLatitude(), address.getLongitude()),
                    "That place is very awesome and I need to make that string long to test the text view",
                    null, "", new HashSet<TrashBox.Category>()), Place.TRASHBOX);
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
            Toast.makeText(bar.parentActivity, "Menu in development ¯\\_(ツ)_/¯", Toast.LENGTH_SHORT).show();
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
        }
    }
}
