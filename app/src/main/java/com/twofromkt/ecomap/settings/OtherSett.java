package com.twofromkt.ecomap.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.activities.MapActivity;

public class OtherSett extends android.support.v4.app.Fragment {

    final MapActivity mapActivity;

    public OtherSett(MapActivity mapActivity) {
        this.mapActivity = mapActivity;
    }
    @Override
    public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstance) {
        return li.inflate(R.layout.other_sett_fragment, null);
    }
}