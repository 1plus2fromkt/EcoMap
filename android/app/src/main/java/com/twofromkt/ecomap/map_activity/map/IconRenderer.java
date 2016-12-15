package com.twofromkt.ecomap.map_activity.map;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.twofromkt.ecomap.place_types.Place;
import com.twofromkt.ecomap.place_types.TrashBox;

class IconRenderer extends DefaultClusterRenderer<MapClusterItem> {

    IconRenderer(Context context, GoogleMap map, ClusterManager<MapClusterItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MapClusterItem item, MarkerOptions markerOptions) {
        Place p = item.getPlace();
        if (p instanceof TrashBox) {
            TrashBox box = (TrashBox) p;
            Bitmap icon = MarkerGenerator.getIcon(box.getChosenCategories());
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        } else {
            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }
}
