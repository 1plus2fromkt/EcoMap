package com.twofromkt.ecomap.map_activity.map_view;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Pair;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.twofromkt.ecomap.db.Place;
import com.twofromkt.ecomap.map_activity.MapActivity;
import com.twofromkt.ecomap.map_activity.MapActivityUtil;

import java.util.ArrayList;

import static com.twofromkt.ecomap.util.Util.activeMarkers;
import static com.twofromkt.ecomap.util.Util.moveMap;

class MapAdapter implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private MapView map;

    MapAdapter(MapView map) {
        this.map = map;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        MapActivityUtil.hideBottomList(map.parentActivity);
        MapActivityUtil.showBottomInfo(map.parentActivity, true);
        Place p = null;
        for (ArrayList<Pair<Marker, ? extends Place>> ac : activeMarkers) {
            for (Pair<Marker, ? extends Place> x : ac)
                if (x.first.equals(marker)) {
                    p = x.second;
                    break;
                }
        }
//        map.parentActivity.name.setText(p.name);
//        map.parentActivity.category_name.setText(p.getClass().getName()); //TODO
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map.mMap = googleMap;
        map.mMap.setOnMarkerClickListener(this);
        if (map.startPos != null) {
            moveMap(map.mMap, map.startPos);
        }
        if (ActivityCompat.checkSelfPermission(
                map.parentActivity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(map.parentActivity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MapActivity.GPS_REQUEST);
            return;
        }
        MapActivityUtil.addLocationSearch(map.parentActivity, map.mMap);
    }
}
