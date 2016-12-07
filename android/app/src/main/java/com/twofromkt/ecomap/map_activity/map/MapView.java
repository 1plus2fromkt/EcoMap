package com.twofromkt.ecomap.map_activity.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.PlaceTypes.Place;
import com.twofromkt.ecomap.map_activity.MapActivity;

import java.util.ArrayList;
import java.util.List;

import static com.twofromkt.ecomap.util.LocationUtil.distanceLatLng;
import static com.twofromkt.ecomap.util.LocationUtil.getLatLng;

public class MapView extends RelativeLayout {

    MapActivity parentActivity;

    GoogleMap mMap;
    CameraPosition startPos;
    SupportMapFragment mapFragment;
    MapAdapter adapter;
    MapUtil util;

    Criteria criteria = new Criteria();
    LocationManager locationManager;

    FloatingActionButton locationButton;

    boolean hasCustomLocation;
    private boolean locationButtonUp;

    public static final float MAPZOOM = 14;

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.element_map, this);
    }

    public void attach(MapActivity parentActivity, FragmentManager fragmentManager,
                       boolean retainInstance) {
        this.parentActivity = parentActivity;
        criteria = new Criteria();
        locationManager = (LocationManager) parentActivity.getSystemService(Context.LOCATION_SERVICE);
        mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.setRetainInstance(retainInstance);
        adapter = new MapAdapter(this);
        locationButton = (FloatingActionButton) findViewById(R.id.location_button);
        locationButton.setOnClickListener(adapter);
        util = new MapUtil(this);
        mapFragment.getMapAsync(adapter);
    }

    public GoogleMap getMap() {
        return mMap;
    }

    public void searchNearCafe(boolean animate) {
        util.searchNearCafe(animate);
    }

    public void searchNearTrashes(boolean animate) {
        util.searchNearTrashes(animate);
    }

    public void focusOnMarker(Pair<Marker, ? extends Place> a) {
        util.focusOnMarker(a);
    }

    public Marker addMarker(Place x, int num) {
        return util.addMarker(x, num);
    }

    public <T extends Place> void addMarkers(ArrayList<T> p, CameraUpdate cu, int num,
                                             boolean animate) {
        util.addMarkers(p, cu, num, animate);
    }

    public void clearMarkers(int num) {
        util.clearMarkers(num);
    }

    public void moveUpLocationButton() {
        if (!locationButtonUp) {
            locationButton.setY(locationButton.getY() - parentActivity.bottomSheet.getPeekHeight());
            locationButtonUp = true;
        }
    }

    public void moveDownLocationButton() {
        if (locationButtonUp) {
            locationButton.setY(locationButton.getY() + parentActivity.bottomSheet.getPeekHeight());
            locationButtonUp = false;
        }
    }

    /**
     * Find the last location where the device was noticed
     *
     * @return last known location
     */
    public Location getLocation() {
        if (ActivityCompat.checkSelfPermission(parentActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(parentActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("permissions denied");
            return null;
        }
        return locationManager.getLastKnownLocation(
                locationManager.getBestProvider(criteria, false));
    }

    /**
     * Find the closest to the current location address
     * matching the request
     *
     * @param request an address to search for
     * @return the closest address matching the request
     */
    public Address findNearestAddress(String request) {
        Geocoder gc = new Geocoder(parentActivity);
        List<Address> addresses;
        try {
            addresses = gc.getFromLocationName(request, 50);
        } catch (Exception e) {
            return null;
        }
        if (addresses.size() == 0) {
            return null;
        }
        Address result = addresses.get(0);
        Location myLocation = getLocation();
        LatLng myCoords = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        double currDist = distanceLatLng(
                new LatLng(result.getLatitude(), result.getLongitude()), myCoords);
        for (Address a : addresses) {
            if (distanceLatLng(new LatLng(a.getLatitude(), a.getLongitude()), myCoords) < currDist) {
                currDist = distanceLatLng(new LatLng(a.getLatitude(), a.getLongitude()), myCoords);
                result = a;
            }
        }
        return result;
    }

    public void moveMap(CameraPosition pos) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    public void addLocationSearch() {
        util.addLocationSearch(parentActivity);
    }

    public static ArrayList<ArrayList<Pair<Marker, ? extends Place>>> getActiveMarkers() {
        return MapUtil.activeMarkers;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superSaved = super.onSaveInstanceState();
        return new SavedMap(superSaved, locationButtonUp, hasCustomLocation);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable savedState) {
        SavedMap savedMap = (SavedMap) savedState;
        super.onRestoreInstanceState(savedMap.getSuperState());
        locationButtonUp = savedMap.getLocationButtonUp();
        if (locationButtonUp) {
            locationButtonUp = false;
            moveUpLocationButton();
        }
        hasCustomLocation = savedMap.hasCustomLocation();
    }
}