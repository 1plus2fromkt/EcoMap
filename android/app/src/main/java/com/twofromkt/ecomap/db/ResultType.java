package com.twofromkt.ecomap.db;

import com.google.android.gms.maps.CameraUpdate;
import com.twofromkt.ecomap.PlaceTypes.Place;

import java.util.ArrayList;

public class ResultType {
    public CameraUpdate cu;
    public ArrayList<? extends Place> res;
    public int number;

    public ResultType(CameraUpdate cu, ArrayList<? extends Place> res, int number) {
        this.cu = cu;
        this.res = res;
        this.number = number;
    }
}
