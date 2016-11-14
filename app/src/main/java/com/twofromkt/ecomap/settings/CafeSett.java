package com.twofromkt.ecomap.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.activities.MapActivity;

public class CafeSett extends android.support.v4.app.Fragment {
    final MapActivity mapActivity;

    public CafeSett(MapActivity mapActivity) {
        this.mapActivity = mapActivity;
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstance) {
        return li.inflate(R.layout.cafe_sett_fragment, null);
    }
}
