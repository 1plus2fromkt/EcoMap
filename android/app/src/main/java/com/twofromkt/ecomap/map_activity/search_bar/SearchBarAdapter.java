package com.twofromkt.ecomap.map_activity.search_bar;

import android.location.Address;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.twofromkt.ecomap.map_activity.map.MapView;
import com.twofromkt.ecomap.place_types.Place;
import com.twofromkt.ecomap.place_types.TrashBox;
import com.twofromkt.ecomap.map_activity.MapActivityUtil;
import com.twofromkt.ecomap.util.LocationUtil;

import java.util.HashSet;

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
            MapActivityUtil.closeKeyboard(bar.parentActivity);
            bar.parentActivity.map.moveMap(LocationUtil.fromLatLngZoom(
                    address.getLatitude(), address.getLongitude(), MapView.MAPZOOM));
        }
        return true;
    }

    @Override
    public void onClick(View v) {
//        for (int i = 0; i < CATEGORIES_N; i++) {
//            if (v == bar.checkboxButtons[i]) {
//                bar.util.setChosen(i, !bar.chosenCheck[i], true);
//                return;
//            }
//        }
        if (v == bar.openMenuButton) {
            Toast.makeText(bar.parentActivity, "Menu in development ¯\\_(ツ)_/¯", Toast.LENGTH_SHORT).show();
//            bar.parentActivity.drawerLayout.openDrawer(act.nv);
            return;
        }
    }
}
