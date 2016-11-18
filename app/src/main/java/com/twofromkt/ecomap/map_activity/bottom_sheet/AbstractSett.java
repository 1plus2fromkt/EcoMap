package com.twofromkt.ecomap.map_activity.bottom_sheet;

import com.twofromkt.ecomap.map_activity.MapActivity;

/**
 * Created by nikita on 11/17/16.
 */

public abstract class AbstractSett extends android.support.v4.app.Fragment {
    MapActivity mapActivity;

    public void setMapActivity(MapActivity act) {
        this.mapActivity = act;
    }
}
