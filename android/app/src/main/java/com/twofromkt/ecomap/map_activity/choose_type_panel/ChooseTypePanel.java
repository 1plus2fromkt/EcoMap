package com.twofromkt.ecomap.map_activity.choose_type_panel;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.twofromkt.ecomap.R;
import com.twofromkt.ecomap.map_activity.MapActivity;
import com.twofromkt.ecomap.map_activity.map.MapView;
import com.twofromkt.ecomap.place_types.Place;

import java.lang.reflect.Field;

import static com.twofromkt.ecomap.Consts.CATEGORIES_NUMBER;

/**
 * This element extends LinearLayout. This is static on the screen.
 * It holds a RelativeLayout which is not static and can be animated or dragged
 * from OPENED state to CLOSED state.
 * <p>
 * Now the internal RelativeLayout contains three buttons and cannot be accessed.
 * This will be changed. TODO
 * <p>
 * On both sides of the element there are Side views. The left one is animated
 * when state of the element is changed. Now panel can only slide from right to left.
 * This will be changed. TODO
 */
public class ChooseTypePanel extends LinearLayout {

    MapActivity parentActivity;
    LinearLayout panelLayout;
    RelativeLayout holderLayout;
    ImageButton[] typeButtons;
    Side leftSide, rightSide;

    ValueAnimator animator;

    boolean animating, showing, sliding;
    /**
     * This is needed to determine where to slide when user releases finger.
     */
    float lastX;
    /**
     * 0 for left, 1 for right
     */
    int direction;
    boolean[] chosenTypes;
    float panelOffset;
    /**
     * The coordinate of the touch performed on the panel. Needed for sliding
     */
    float slidingOffset;
    float sideWidth;

    /**
     * Offset from the right side in OPENED state in dip.
     * This should probably be changeable
     */
    static final private float OFFSET_RIGHT = 10;
    /**
     * Offset from the left side in px. This is set by external layout
     * so it cannot be changed and should be initialized by OnGlobalLayoutListener
     */
    static private float OFFSET_LEFT;
    /**
     * Full animation duration in ms.
     * This should probably be changeable
     */
    static final private long ANIMATION_DURATION = 200;

    static final private int[] IMAGE_IDS = {
            R.mipmap.trashbox_icon, R.mipmap.cafes_icon, R.mipmap.other_icon};
    static final private int[] IMAGE_IDS_CHOSEN = {R.mipmap.trashbox_icon_selected,
            R.mipmap.cafes_icon_selected, R.mipmap.other_icon_chosen};

    public ChooseTypePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.element_choose_type_panel, this);
        holderLayout = (RelativeLayout) findViewById(R.id.choose_type_panel_holder);
        panelLayout = (LinearLayout) findViewById(R.id.choose_type_panel);

        leftSide = new Side(context);
        rightSide = new Side(context);
        holderLayout.addView(leftSide);
        holderLayout.addView(rightSide);

        // need this to set element's sizes properly
        // we get panelLayout height and y because for some reasons this.height includes margin
        leftSide.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                leftSide.setSize(holderLayout.getHeight());
                leftSide.setX(0);
                leftSide.transform(1);
                leftSide.invalidate();
                leftSide.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        rightSide.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rightSide.setSize(holderLayout.getHeight());
                rightSide.setX(holderLayout.getWidth()
                        - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, OFFSET_RIGHT,
                        getResources().getDisplayMetrics()));
                rightSide.transform(1);
                rightSide.invalidate();
                rightSide.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        holderLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                panelLayout.setVisibility(VISIBLE);
                // side width is always half of it's height
                sideWidth = holderLayout.getHeight() / 2;
                holderLayout.setX(getWidth() - sideWidth);
                OFFSET_LEFT = getX();

                ViewGroup.LayoutParams params = holderLayout.getLayoutParams();
                // this should probably be changed to something more clever
                // but I don't know how :(
                // now it is element width + width of side - right offset
                params.width = getWidth() + (int) sideWidth
                        - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, OFFSET_RIGHT,
                        getResources().getDisplayMetrics());
                holderLayout.setLayoutParams(params);
                holderLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    public void attach(MapActivity parentActivity) {
        this.parentActivity = parentActivity;
        typeButtons = new ImageButton[CATEGORIES_NUMBER];
        chosenTypes = new boolean[CATEGORIES_NUMBER];
        for (int i = 0; i < CATEGORIES_NUMBER; i++) {
            try {
                typeButtons[i] = (ImageButton) findViewById((Integer)
                        R.id.class.getField("type_button" + (i + 1)).get(null));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        leftSide.setImage(BitmapFactory.decodeResource(getResources(), R.mipmap.choose_panel_arrow_left));
        rightSide.setImage(BitmapFactory.decodeResource(getResources(), R.mipmap.choose_panel_arrow_right));
        animating = showing = false;
        panelLayout.setVisibility(INVISIBLE);

        setListeners();
    }



    private void setListeners() {
        for (int i = 0; i < CATEGORIES_NUMBER; i++) {
            final int index = i;
            final int finalI = i;
            typeButtons[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setChosen(index, !chosenTypes[index], true);
                    if (finalI == 2) { //TODO replace it
                        parentActivity.updateDatabase();
                    }
                    parentActivity.bottomSheet.setNewListPagerAdapter();
                }
            });
        }

        holderLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                stopAnimation();
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    float x = event.getRawX() - slidingOffset - OFFSET_LEFT;
//                    float len = getWidth() - sideWidth;
                    float len = getWidth();
                    // we don't want panel move to the left side of the screen or
                    // to be hidden on the right side
                    if (x >= getNeededX(PanelState.OPENED) && x <= getNeededX(PanelState.CLOSED)) {
                        holderLayout.setX(x);
                    } else {
                        slidingOffset = event.getX();
                    }
                    leftSide.transform((x + sideWidth) / len);
                    direction = event.getRawX() < lastX ? 0 : 1;
                    lastX = event.getRawX();
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    sliding = true;
                    slidingOffset = event.getX();
                    direction = showing ? 1 : 0;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    sliding = false;
                    showing = direction == 0; // sliding left
                    if (showing) {
                        animateState(ANIMATION_DURATION, PanelState.OPENED);
                    } else {
                        animateState(ANIMATION_DURATION, PanelState.CLOSED);
                    }
                    return true;
                }
                leftSide.setX(getSideOffset(SideType.LEFT));
                leftSide.invalidate();
                return true;
            }
        });
    }

    /**
     * Animate panel to slide to the specified state
     *
     * @param duration Length of full animation (from one side to another) in ms
     * @param state    Final state for the animation
     */
    private void animateState(long duration, PanelState state) {
        if (animating) {
            animator.cancel();
            animating = false;
        }
        float currentX = holderLayout.getX();
        float endX = getNeededX(state);
        animator = ValueAnimator.ofFloat(currentX, endX);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float state = (float) animation.getAnimatedValue();
                holderLayout.setX(state);
                leftSide.setX(getSideOffset(SideType.LEFT));
            }
        });
        float partOfWidth = Math.abs(getNeededX(state) - currentX) / holderLayout.getWidth();
        duration *= partOfWidth;
        animator.setDuration(duration);
        animator.start();
        leftSide.animateState(duration, state);
        animating = true;
    }

    private void stopAnimation() {
        if (animating) {
            animator.cancel();
            leftSide.stopAnimation();
            animating = false;
        }
    }

    /**
     * Get offset for the side bar
     *
     * @param type left or right, 0 for left, 1 for right
     */
    private float getSideOffset(SideType type) {
        if (type == SideType.LEFT) {
            return sideWidth - leftSide.getActualWidth();
        } else {
            return 0;
        }
    }

    /**
     * Calculate x coordinate of the holder layout we want in the given state
     *
     * @param state Wanted state
     * @return Wanted x coordinate in pixels
     */
    private float getNeededX(PanelState state) {
        switch (state) {
            case OPENED:
                return -sideWidth;
            case CLOSED:
                return getWidth() - sideWidth;
            default:
                return 0;
        }
    }

    public void hide() {
        if (showing) {
            //TODO
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
            setX(0);
            panelLayout.setVisibility(VISIBLE);
        }
        chosenTypes = savedPanel.getChosenTypes();
        for (int i = 0; i < CATEGORIES_NUMBER; i++) {
            setChosen(i, chosenTypes[i], false);
        }
    }

    /**
     * Set a checkbox state. In case to just change the state without interacting with
     * other components call with activateMap = false.
     * If place are not loaded yet (due to an error or because method wasn't called),
     * starts loading and returns.
     *
     * @param index       index of checkbox to be chosen
     * @param state       true if checkbox should be chosen, false otherwise
     * @param activateMap true if the method should change element_map to show new objects
     */
    public void setChosen(int index, boolean state, boolean activateMap) {
        chosenTypes[index] = state;
        typeButtons[index].setImageBitmap(BitmapFactory.decodeResource(
                getResources(),
                chosenTypes[index] ? IMAGE_IDS_CHOSEN[index] : IMAGE_IDS[index]));
        if (!activateMap) {
            return;
        }
        if (state) {
            parentActivity.bottomSheet.show();
            parentActivity.bottomSheet.focusOnTab(index);
            if (!parentActivity.map.placesLoaded) {
                parentActivity.map.loadAllPlaces(); //TODO call setChosen after loading
                return;
            }
            if (index == Place.TRASHBOX) {
                parentActivity.map.showTrashMarkers();
            } else if (index == Place.ECOMOBILE) {
                parentActivity.map.showEcomobileMarkers();
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

    protected enum PanelState {
        OPENED, CLOSED
    }

    private enum SideType {
        LEFT, RIGHT
    }
}