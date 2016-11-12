package com.twofromkt.ecomap.activities;

import android.animation.Animator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.db.Place;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.ViewAnimationUtils;

import static com.twofromkt.ecomap.util.LocationUtil.getLatLng;
import static com.twofromkt.ecomap.util.Util.activeMarkers;

public class MapActivityUtil {

//    static Class[] intToClass = new Class[]{TrashBox.class, Cafe.class, Place.class};
    static final float[] ALPHAS = new float[]{(float) 0.6, 1};


    static void showBottomInfo(MapActivity act, boolean showSheet) {
        act.navigationButton.setVisibility(View.VISIBLE);
        act.locationButton.setVisibility(View.INVISIBLE);
        act.floatingMenu.setVisibility(View.INVISIBLE);
        if (showSheet) {
            act.bottomInfo.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    static void showBottomList(MapActivity act) {
        act.locationButton.setVisibility(View.INVISIBLE);
        act.bottomList.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    static void showBottomList(MapActivity act, ArrayList<? extends Place> data, int num) {
        if (act.bottomList.getState() == BottomSheetBehavior.STATE_HIDDEN)
            showBottomList(act);
    }

    static void hideBottomInfo(MapActivity act) {
        act.navigationButton.setVisibility(View.INVISIBLE);
        act.locationButton.setVisibility(View.VISIBLE);
        act.floatingMenu.setVisibility(View.VISIBLE);
        act.bottomInfo.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    static void hideBottomList(MapActivity act) {
        act.locationButton.setVisibility(View.VISIBLE);
        act.bottomList.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    static void closeKeyboard(MapActivity act) {
        View view = act.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    static void closeFloatingMenu(MapActivity act) {
        act.floatingMenu.close(true);
    }

    static boolean isBottomOpened(MapActivity act) {
        return act.bottomInfo.getState() != BottomSheetBehavior.STATE_HIDDEN;
    }

    static Marker addMarker(GoogleMap mMap, Place x, int num) {
        Marker m = mMap.addMarker(new MarkerOptions().position(getLatLng(x.location)).title(x.name));
        activeMarkers.get(num).add(new Pair<>(m, x));
        return m;
    }

    static <T extends Place> void addMarkers(ArrayList<T> p, CameraUpdate cu, GoogleMap mMap, int num) {
        clearMarkers(num);
        ArrayList<LatLng> pos = new ArrayList<>();
        for (Place place : p) {
            addMarker(mMap, place, num);
            pos.add(getLatLng(place.location));
        }
        if (pos.size() > 0) {
            mMap.animateCamera(cu);
        }
    }

    static void clearMarkers(int num) {
        if (num == -1)
            return;
        for (Pair<Marker, ? extends Place> m : activeMarkers.get(num))
            m.first.remove();
        activeMarkers.get(num).clear();
    }

    static void addLocationSearch(MapActivity act, GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(act,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
    }

    public static void expand(final View myView, final View another) {
        myView.setVisibility(View.VISIBLE);

        int dx = myView.getWidth();
        int dy = myView.getHeight();
        float finalRadius = (float) Math.hypot(dx, dy);
        myView.setVisibility(View.VISIBLE);
        Animator animator =
                ViewAnimationUtils.createCircularReveal(myView, myView.getWidth(), 0, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                another.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(400);
        animator.start();
    }

    public static void collapse(final View myView, final View another) {

        int dx = myView.getWidth();
        int dy = myView.getHeight();
        float finalRadius = (float) Math.hypot(dx, dy);
        myView.setVisibility(View.VISIBLE);
        Animator animator =
                ViewAnimationUtils.createCircularReveal(myView, myView.getRight(), 0, finalRadius, 0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(400);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                another.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                myView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }


}
