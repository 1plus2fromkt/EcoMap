package com.twofromkt.ecomap.map_activity.map;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.twofromkt.ecomap.db.Place;
import com.twofromkt.ecomap.map_activity.MapActivity;

import java.util.ArrayList;

import static com.twofromkt.ecomap.util.LocationUtil.fromLatLngZoom;

class MapAdapter implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        FloatingActionButton.OnClickListener {

    private MapView map;

    MapAdapter(MapView map) {
        this.map = map;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        map.parentActivity.bottomSheet.hide();
        map.parentActivity.bottomInfo.show(true);
        Place p = null;
        for (ArrayList<Pair<Marker, ? extends Place>> ac : MapView.getActiveMarkers()) {
            for (Pair<Marker, ? extends Place> x : ac) {
                if (x.first.equals(marker)) {
                    p = x.second;
                    break;
                }
            }
        }
        if (p == null) {
            Toast.makeText(map.parentActivity, "Unable to find place", Toast.LENGTH_SHORT).show();
            return false;
        }
        map.parentActivity.bottomInfo.setPlace(p);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map.mMap = googleMap;
        map.mMap.setOnMarkerClickListener(this);
        if (map.startPos != null) {
            map.moveMap(map.startPos);
        }
        if (ActivityCompat.checkSelfPermission(
                map.parentActivity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(map.parentActivity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MapActivity.GPS_REQUEST);
            return;
        }
        map.util.addLocationSearch(map.parentActivity);
//        map.mMap.setMyLocationEnabled(true);
//        UiSettings ui = map.mMap.getUiSettings();
//        ui.setZoomControlsEnabled(true);
//        map.mMap.setPadding(0, 0, 0, 600);
    }

    @Override
    public void onClick(View v) {
        if (v == map.locationButton) {
            Location location = map.getLocation();
            if (ActivityCompat.checkSelfPermission(
                    map.parentActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                return;
            }

            if (location != null) {
                map.moveMap(fromLatLngZoom(location.getLatitude(), location.getLongitude(), MapView.MAPZOOM));
            }
        }
    }
}
