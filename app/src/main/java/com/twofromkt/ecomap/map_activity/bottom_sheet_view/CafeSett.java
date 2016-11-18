package com.twofromkt.ecomap.map_activity.bottom_sheet_view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twofromkt.ecomap.R;

public class CafeSett extends AbstractSett{

    public CafeSett() {
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstance) {
        return li.inflate(R.layout.cafe_sett_fragment, null);
    }
}
