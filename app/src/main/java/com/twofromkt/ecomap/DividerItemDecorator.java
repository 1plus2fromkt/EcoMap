package com.twofromkt.ecomap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by alexey.nikitin on 27.09.16.
 */

public final class DividerItemDecorator extends RecyclerView.ItemDecoration {
    private final float dividerHeight;
    private final Paint p;

    public DividerItemDecorator(Context context) {
        dividerHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
        p = new Paint();
        p.setColor(0xFF999999);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position > 0) {
            outRect.top += dividerHeight;
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            int top = child.getTop();
            if (top < -dividerHeight) {
                continue;
            }

            int position = parent.getChildAdapterPosition(child);
            if (position == 0) {
                continue;
            }

            int y = (int) (top + child.getTranslationY() - dividerHeight / 2);
            c.drawLine(0, y, parent.getWidth(), y, p);
        }
    }
}
