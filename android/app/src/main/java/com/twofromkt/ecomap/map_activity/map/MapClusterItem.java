package com.twofromkt.ecomap.map_activity.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.twofromkt.ecomap.PlaceTypes.Place;
import com.twofromkt.ecomap.util.LocationUtil;

public class MapClusterItem implements ClusterItem {
    Place place;

    MapClusterItem(Place place) {
        this.place = place;
    }

    @Override
    public LatLng getPosition() {
        return LocationUtil.getLatLng(place.location);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MapClusterItem && equals((MapClusterItem) o);
    }

    public boolean equals(MapClusterItem o) {
        return place.equals(o.place);
    }
}