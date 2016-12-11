package com.twofromkt.ecomap.map_activity.map;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.List;

class MapMultiListener implements GoogleMap.OnCameraIdleListener {
    private List<GoogleMap.OnCameraIdleListener> listeners;

    MapMultiListener() {
        listeners = new ArrayList<>();
    }

    void addListener(GoogleMap.OnCameraIdleListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onCameraIdle() {
        for (GoogleMap.OnCameraIdleListener listener : listeners) {
            listener.onCameraIdle();
        }
    }
}