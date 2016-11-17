package com.twofromkt.ecomap.map_activity.bottom_sheet_view.a;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twofromkt.ecomap.R;

public class OtherSett extends AbstractSett {
    public OtherSett() {
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstance) {
        return li.inflate(R.layout.other_sett_fragment, null);
    }
}