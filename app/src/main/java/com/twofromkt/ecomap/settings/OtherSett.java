package com.twofromkt.ecomap.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.map_activity.MapActivity;

public class OtherSett extends android.support.v4.app.Fragment {

    MapActivity mapActivity;

    public OtherSett() {
    }

    public void setMapActivity(MapActivity act) {
        this.mapActivity = act;
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstance) {
        return li.inflate(R.layout.other_sett_fragment, null);
    }
}