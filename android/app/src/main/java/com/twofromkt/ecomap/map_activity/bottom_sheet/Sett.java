package com.twofromkt.ecomap.map_activity.bottom_sheet;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twofromkt.ecomap.DividerItemDecorator;
import com.twofromkt.ecomap.place_types.TrashBox;
import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.map_activity.MapActivity;

import java.util.Arrays;

import static com.twofromkt.ecomap.Consts.TRASH_TYPES_NUMBER;

public abstract class Sett extends LinearLayout {
    MapActivity mapActivity;

    public Sett(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMapActivity(MapActivity act) {
        this.mapActivity = act;
    }

//    public static class CafeSett extends Sett {

//        public CafeSett() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstance) {
//            return li.inflate(R.layout.fragment_cafe_sett, null);
//        }
//    }



//    public static class OtherSett extends Sett {

//        public OtherSett() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstance) {
//            return li.inflate(R.layout.fragment_other_sett, null);
//        }
//    }
}
