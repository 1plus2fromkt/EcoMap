package com.twofromkt.ecomap.map_activity.choose_type_panel;

import android.animation.Animator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.map_activity.MapActivity;
import com.twofromkt.ecomap.place_types.Place;

import static com.twofromkt.ecomap.Consts.CATEGORIES_NUMBER;

public class ChooseTypePanel extends LinearLayout {

    MapActivity parentActivity;
    RelativeLayout panel;
    ImageButton[] typeButtons;
    ImageButton openButton;

    boolean animating, showing;
    boolean[] chosenTypes;
    float panelOffset;

    final private static int[] imageIds = {R.mipmap.trashbox_type, R.mipmap.cafes_icon, R.mipmap.other_icon};
    final private static int[] imageIdsChosen = {R.mipmap.trashbox_type_chosen,
            R.mipmap.cafes_icon_chosen, R.mipmap.other_icon_chosen};

    public ChooseTypePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.element_choose_type_panel, this);
    }

    public void attach(MapActivity parentActivity) {
        this.parentActivity = parentActivity;
        panel = (RelativeLayout) findViewById(R.id.choose_type_panel);
        openButton = (ImageButton) findViewById(R.id.show_choose_type_panel);
        typeButtons = new ImageButton[CATEGORIES_NUMBER];
        chosenTypes = new boolean[CATEGORIES_NUMBER];
        typeButtons[0] = (ImageButton) findViewById(R.id.type_button1);
        typeButtons[1] = (ImageButton) findViewById(R.id.type_button2);
        typeButtons[2] = (ImageButton) findViewById(R.id.type_button3);
        animating = showing = false;
        panel.setVisibility(INVISIBLE);

        setListeners();
    }

    private void setListeners() {
        openButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (animating) return;
                if (panel.getVisibility() == INVISIBLE) {
                    panelOffset = panel.getX();
                    panel.setX(-panel.getWidth());
                    panel.setVisibility(VISIBLE);
                }
                ViewPropertyAnimator animator = panel.animate();
                animator.setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        animating = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animating = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                int duration = 400;
                animator.setDuration(duration);
                ViewPropertyAnimator buttonAnimator = openButton.animate();
                buttonAnimator.setDuration(duration);
                animator.xBy((showing ? -1 : 1) * (panel.getWidth() + panelOffset));
                buttonAnimator.rotationBy((showing ? 1 : -1) * 90);
                showing = !showing;
            }
        });

        for (int i = 0; i < 3; i++) {
            final int index = i;
            final int finalI = i;
            typeButtons[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setChosen(index, !chosenTypes[index], true);
                    if (finalI == 2) { //TODO replace it!!!
                        parentActivity.updateDatabase();
                    }
                }
            });
        }
    }

    public void hide() {
        if (showing) {
            openButton.callOnClick();
        }
    }

    public boolean isOpened() {
        return showing;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superSaver = super.onSaveInstanceState();
        return new SavedChooseTypePanel(superSaver, showing, chosenTypes, panelOffset);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable savedState) {
        SavedChooseTypePanel savedPanel = (SavedChooseTypePanel) savedState;
        super.onRestoreInstanceState(savedPanel.getSuperState());
        panelOffset = savedPanel.getPanelOffset();
        showing = savedPanel.getShowing();
        if (showing) {
            panel.setX(0);
            panel.setVisibility(VISIBLE);
            openButton.setRotation(-90);
        }
        chosenTypes = savedPanel.getChosenTypes();
        for (int i = 0; i < CATEGORIES_NUMBER; i++) {
            setChosen(i, chosenTypes[i], false);
        }
    }

    protected /**
     * Set a checkbox state. In case to just change the state without interacting with
     * other components call with activateMap = false
     *
     * @param index index of checkbox to be chosen
     * @param state true if checkbox should be chosen, false otherwise
     * @param activateMap true if the method should change element_map to show new objects
     */
    void setChosen(int index, boolean state, boolean activateMap) {
        chosenTypes[index] = state;
        typeButtons[index].setImageBitmap(BitmapFactory.decodeResource(
                getResources(),
                chosenTypes[index] ? imageIdsChosen[index] : imageIds[index]));
        if (!activateMap) {
            return;
        }
        if (!parentActivity.map.placesLoaded) {
            parentActivity.map.loadAllPlaces();
            return;
        }
        if (state) {
            parentActivity.bottomSheet.show();
            parentActivity.bottomSheet.focusOnTab(index);
            if (index == Place.TRASHBOX) {
                parentActivity.map.showTrashMarkers();
            } else if (index == Place.CAFE) {
                parentActivity.map.showCafeMarkers();
            }
        } else {
            parentActivity.map.clearMarkers(index);
            if (allUnchecked()) {
                parentActivity.bottomSheet.hide();
                parentActivity.bottomInfo.hide();
            }
        }
    }

    private boolean allUnchecked() {
        boolean ans = true;
        for (int i = 0; i < CATEGORIES_NUMBER; i++) {
            ans &= !chosenTypes[i];
        }
        return ans;
    }

    public boolean isChosen(int i) {
        return chosenTypes[i];
    }

}