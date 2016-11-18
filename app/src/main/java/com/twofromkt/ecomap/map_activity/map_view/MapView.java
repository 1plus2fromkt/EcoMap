package com.twofromkt.ecomap.map_activity.map_view;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.Loader;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.db.GetPlaces;
import com.twofromkt.ecomap.db.Place;
import com.twofromkt.ecomap.map_activity.MapActivity;
import com.twofromkt.ecomap.map_activity.MapActivityUtil;

import java.util.ArrayList;

import static com.twofromkt.ecomap.util.Util.moveMap;

public class MapView extends RelativeLayout {

    MapActivity parentActivity;
    GoogleMap mMap;
    CameraPosition startPos;
    SupportMapFragment mapFragment;
    MapAdapter adapter;

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.map, this);
    }

    public void attach(MapActivity parentActivity, FragmentManager fragmentManager,
                       boolean retainInstance) {
        this.parentActivity = parentActivity;
        mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.setRetainInstance(retainInstance);
        adapter = new MapAdapter(this);
        mapFragment.getMapAsync(adapter);
    }

    public GoogleMap getMap() {
        return mMap;
    }

    public void searchNearCafe() {
        Bundle b = createBundle();
        Loader<Pair<CameraUpdate, ArrayList<? extends Place>>> loader;
        b.putInt(GetPlaces.WHICH_PLACE, GetPlaces.CAFE);
        loader = parentActivity.getSupportLoaderManager()
                .restartLoader(MapActivity.LOADER, b, parentActivity.adapter);
        loader.onContentChanged();
        MapActivityUtil.showBottomList(parentActivity);
    }

    public void searchNearTrashes() {
        Bundle bundle = createBundle();
        Loader<Pair<CameraUpdate, ArrayList<? extends Place>>> loader;
        bundle.putInt(GetPlaces.WHICH_PLACE, GetPlaces.TRASH);
        bundle.putBooleanArray(GetPlaces.CHOSEN, parentActivity.bottomSheet.getTrashCategories());
        loader = parentActivity.getSupportLoaderManager()
                .restartLoader(MapActivity.LOADER, bundle, parentActivity.adapter);
        loader.onContentChanged();
    }

    public void focusOnMarker(Pair<Marker, ? extends Place> a) {
        MapActivityUtil.hideBottomList(parentActivity);
        MapActivityUtil.showBottomInfo(parentActivity, true);
        parentActivity.bottomInfo.addInfo(a.second.name, a.second.getClass().getName());
//        moveMap(act.mMap, fromLatLngZoom(a.second.location.val1, a.second.location.val2, MAPZOOM));
    }

    private Bundle createBundle() {
        LatLng curr = mMap.getCameraPosition().target;
        Bundle bundle = new Bundle();
        bundle.putDouble(MapActivity.LAT, curr.latitude);
        bundle.putDouble(MapActivity.LNG, curr.longitude);
        bundle.putInt(GetPlaces.MODE, GetPlaces.NEAR);
        bundle.putFloat(GetPlaces.RADIUS, (float) 1e15);
        return bundle;
    }
}