package com.twofromkt.ecomap.map_activity.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.twofromkt.ecomap.place_types.Place;
import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.map_activity.MapActivity;
import com.twofromkt.ecomap.util.LocationUtil;
import com.twofromkt.ecomap.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.twofromkt.ecomap.util.LocationUtil.distanceLatLng;

public class MapView extends RelativeLayout {

    MapActivity parentActivity;

    GoogleMap mMap;
    CameraPosition startPos;
    SupportMapFragment mapFragment;
    MapAdapter adapter;
    public MapUtil util;

    GoogleApiClient mGoogleClient;
    ClusterManager<MapClusterItem> clusterManager;

    ImageButton locationButton;

    public boolean placesLoaded;
    boolean hasCustomLocation;
    private boolean locationButtonUp;

    public static final float MAPZOOM = 14;
    //TODO replace this somehow
    static Location DEFAULT_LOCATION;

    static {
        // spb coords
        DEFAULT_LOCATION = new Location("");
        DEFAULT_LOCATION.setLatitude(59.93863);
        DEFAULT_LOCATION.setLongitude(30.31413);
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.element_map, this);
    }

    public void attach(MapActivity parentActivity, FragmentManager fragmentManager,
                       boolean retainInstance) {
        this.parentActivity = parentActivity;
        mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.setRetainInstance(retainInstance);
        adapter = new MapAdapter(this);
        locationButton = (ImageButton) findViewById(R.id.location_button);
        locationButton.setOnClickListener(adapter);
        util = new MapUtil(this);
        MarkerGenerator.init(getResources().getDisplayMetrics());
        mGoogleClient = new GoogleApiClient.Builder(parentActivity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(adapter)
                .addOnConnectionFailedListener(adapter)
                .build();
        mGoogleClient.connect();
        mapFragment.getMapAsync(adapter);
    }

    public GoogleMap getMap() {
        return mMap;
    }

    public void showEcomobileMarkers() {
        util.showEcomobileMarkers();
    }

    public void showTrashMarkers() {
        util.showTrashMarkers(true);
    }

    public void focusOnMarker(Util.PlaceWithCoord a) {
        util.focusOnMarker(a);
    }

    public void addMarker(Place x, int type) {
        util.addMarker(x, type);
    }

    public <T extends Place> void addMarkers(ArrayList<T> p, CameraUpdate cu, int num) {
        util.addMarkers(p, cu, num);
    }

    public void clearMarkers(int num) {
        util.clearMarkers(num, true);
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
     * @return last known location or default location if last known is unavaliable
     */
    public Location getLocation() {
        if (ActivityCompat.checkSelfPermission(parentActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(parentActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(parentActivity, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MapActivity.GPS_REQUEST);
            Log.d("MAP", "location denied");
            return DEFAULT_LOCATION;
        }
        Location currLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleClient);
        if (currLocation == null) {
            Log.d("MAP_VIEW", "не получается узнать местоположение");
            return DEFAULT_LOCATION;
        } else {
            return currLocation;
        }
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

    public static ArrayList<ArrayList<Util.PlaceWithCoord>> getAllMarkers() {
        return PlacesHolder.getInstance().getAll();
    }

    public static ArrayList<ArrayList<Util.PlaceWithCoord>> getShownMarkers() {
        return PlacesHolder.getInstance().getShown();
    }

    public static ArrayList<Util.PlaceWithCoord> getAllMarkers(int category) {
        return PlacesHolder.getInstance().getAll(category);
    }

    public static ArrayList<Util.PlaceWithCoord> getShownMarkers(int category) {
        return PlacesHolder.getInstance().getShown(category);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superSaved = super.onSaveInstanceState();
        return new SavedMap(superSaved, locationButtonUp, hasCustomLocation, placesLoaded);
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
        placesLoaded = savedMap.placesLoaded();
    }

    public void onClusterUpdate() {
        clusterManager.cluster();
    }

    /**
     * Starts loader to read full data about the place from database.
     * When load finishes, map will focus on that place.
     *
     * @param id id of place to load
     * @param category number of category if the place
     */
    public void loadPlace(int id, int category) {
        util.loadPlace(id, category);
    }

    public void loadAllPlaces() {
        if (placesLoaded) {
            return;
        }
        util.loadAllPlaces();
    }
}