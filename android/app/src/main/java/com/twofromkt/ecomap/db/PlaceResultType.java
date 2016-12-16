package com.twofromkt.ecomap.db;

import com.google.android.gms.maps.CameraUpdate;
import com.twofromkt.ecomap.place_types.Place;

import java.util.ArrayList;

public class PlaceResultType {
    public CameraUpdate cameraUpdate;
    public ArrayList<? extends Place> res;
    public int number;
    public boolean searchById;
    public boolean loadSuccess;

    PlaceResultType(CameraUpdate cameraUpdate, ArrayList<? extends Place> res, int number,
                    boolean searchById) {
        this.cameraUpdate = cameraUpdate;
        this.res = res;
        this.number = number;
        this.searchById = searchById;
        loadSuccess = true;
    }

    PlaceResultType() {
        loadSuccess = false;
    }
}
