package com.twofromkt.ecomap.db;

import com.google.android.gms.maps.CameraUpdate;
import com.twofromkt.ecomap.place_types.Place;

import java.util.ArrayList;

public class PlaceResultType {
    public CameraUpdate cu;
    public ArrayList<? extends Place> res;
    public int number;
    public boolean searchById;
    public boolean loadSucces;

    PlaceResultType(CameraUpdate cu, ArrayList<? extends Place> res, int number,
                    boolean searchById) {
        this.cu = cu;
        this.res = res;
        this.number = number;
        this.searchById = searchById;
        loadSucces = true;
    }

    PlaceResultType() {
        loadSucces = false;
    }
}
