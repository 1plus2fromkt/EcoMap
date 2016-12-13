package com.twofromkt.ecomap.map_activity.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.twofromkt.ecomap.PlaceTypes.Place;
import com.twofromkt.ecomap.util.LocationUtil;

public class MapClusterItem implements ClusterItem {
    private Place place;

    MapClusterItem(Place place) {
        this.place = place;
    }

    @Override
    public LatLng getPosition() {
        return LocationUtil.getLatLng(place.getLocation());
    }

    public Place getPlace() {
        return place;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MapClusterItem && equals((MapClusterItem) o);
    }

    boolean equals(MapClusterItem o) {
        return place.equals(o.place);
    }
}