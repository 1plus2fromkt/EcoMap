package com.twofromkt.ecomap.map_activity.choose_type_panel;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.v4.animation.ValueAnimatorCompat;
import android.view.View;

import com.twofromkt.ecomap.R;

class Side extends View {

    RectF rect;
    float size;
    /**
     * width to height ratio
     */
    float ratio;
    Context context;
    Bitmap image;
    ValueAnimator animator;
    boolean animating;

    Side(Context context) {
        super(context);
        this.context = context;
        rect = new RectF();
    }

    /**
     * Set side image. This should be called as initializing method
     * @param image image to be drawn as a side
     */
    void setImage(Bitmap image) {
        this.image = image;
    }

    void setSize(float size) {
        this.size = size;
    }

    void transform(float ratio) {
        this.ratio = ratio;
        rect.set(0, 0, size * ratio / 2, size);
    }

    float getActualWidth() {
        return size * ratio / 2;
    }

    void animateState(long duration, ChooseTypePanel.PanelState state) {
        if (animating) {
            animator.cancel();
            animating = false;
        }
        float endRatio = state == ChooseTypePanel.PanelState.OPENED ? 0 : 1;
        animator = ValueAnimator.ofFloat(ratio, endRatio);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float value = (float) animator.getAnimatedValue();
                transform(value);
                invalidate();
            }
        });
        animator.setDuration(duration);
        animator.start();
        animating = true;
    }

    void stopAnimation() {
        if (animating) {
            animator.cancel();
            animating = false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(image, null, rect, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) size, (int) size);
    }
}
