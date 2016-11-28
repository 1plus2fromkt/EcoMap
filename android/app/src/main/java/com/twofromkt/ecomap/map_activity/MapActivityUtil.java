package com.twofromkt.ecomap.map_activity;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class MapActivityUtil {

//    public static boolean isAnimating = false;

    public static void closeKeyboard(MapActivity act) {
        View view = act.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}