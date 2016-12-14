package com.twofromkt.ecomap.map_activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.map_activity.bottom_info.BottomInfoView;
import com.twofromkt.ecomap.map_activity.bottom_sheet.BottomSheetView;
import com.twofromkt.ecomap.map_activity.choose_type_panel.ChooseTypePanel;
import com.twofromkt.ecomap.map_activity.map.MapView;
import com.twofromkt.ecomap.map_activity.search_bar.SearchBarView;

import static com.twofromkt.ecomap.Consts.CATEGORIES_NUMBER;

public class MapActivity extends FragmentActivity {

//    NavigationView nv;

    DrawerLayout drawerLayout;

    public MapActivityAdapter adapter;

    public SearchBarView searchBar;
    public MapView map;
    public BottomInfoView bottomInfo;
    public BottomSheetView bottomSheet;
    public ChooseTypePanel typePanel;

    public static final int GPS_REQUEST = 111, LOADER = 42;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initFields();
    }

    private void initFields() {
        map = (MapView) findViewById(R.id.map_view);
        map.attach(this, getSupportFragmentManager(), true);
        searchBar = (SearchBarView) findViewById(R.id.search_bar);
        searchBar.attach(this);
        bottomInfo = (BottomInfoView) findViewById(R.id.binfo);
        bottomInfo.attach(this);
        bottomSheet = (BottomSheetView) findViewById(R.id.bsheet);
        bottomSheet.attach(getSupportFragmentManager(), this);
        typePanel = (ChooseTypePanel) findViewById(R.id.choose_type);
        typePanel.attach(this);

        adapter = new MapActivityAdapter(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

//        nv = (NavigationView) findViewById(R.id.nav_view);

        bottomInfo.hide();
        bottomSheet.hide();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case GPS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    map.addLocationSearch();
                }
        }
    }

    private boolean checkMarkers() {
        for (int i = 0; i < CATEGORIES_NUMBER; i++) {
            if (MapView.getAllMarkers().get(i).size() > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (bottomInfo.isExpanded()) {
            bottomInfo.collapse();
        } else if (bottomInfo.isCollapsed()) {
            bottomInfo.hide();
            bottomSheet.collapse();
        } else if (bottomSheet.isExpanded()) {
            bottomSheet.collapse();
        } else if (typePanel.isOpened()) {
            typePanel.hide();
        } else {
            super.onBackPressed();
        }
    }

}