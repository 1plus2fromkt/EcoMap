package com.twofromkt.ecomap.map_activity.map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;

import com.twofromkt.ecomap.R;

class MarkerGenerator {

    private static int CATEGORIES_NUMBER = 11;
    private static Paint[] CATEGORIES_COLORS;
    private static Paint MAIN_COLOR, CENTER_FILL_COLOR;
    private static int widthInDip = 25;
    private static int width, height, offset, stickWidth;
    private static SparseArray<Bitmap> icons;
    private static Bitmap ecomobileIcon;

    /**
     * Default width value in px in case if init was not called
     */
    private static final int DEFAULT_WIDTH = 50;

    private static DisplayMetrics metrics;

    static {
        recount(widthInDip);

        icons = new SparseArray<>();
        CATEGORIES_COLORS = new Paint[CATEGORIES_NUMBER];
        for (int i = 0; i < CATEGORIES_NUMBER; i++) {
            CATEGORIES_COLORS[i] = new Paint();
        }
        MAIN_COLOR = new Paint();
        CENTER_FILL_COLOR = new Paint();

        setColors();
        setAntiAliasing(true);
    }

    //TODO move this to config file
    private static void setColors() {
        CATEGORIES_COLORS[0].setARGB(255, 86, 142, 204);
        CATEGORIES_COLORS[1].setARGB(255, 104, 186, 82);
        CATEGORIES_COLORS[2].setARGB(255, 247, 130, 36);
        CATEGORIES_COLORS[3].setARGB(255, 250, 100, 86);
        CATEGORIES_COLORS[4].setARGB(255, 243, 88, 241);
        CATEGORIES_COLORS[5].setARGB(255, 116, 238, 242);
        CATEGORIES_COLORS[6].setARGB(255, 58, 57, 57);
        CATEGORIES_COLORS[7].setARGB(255, 198, 151, 69);
        CATEGORIES_COLORS[8].setARGB(255, 152, 76, 255);
        CATEGORIES_COLORS[9].setARGB(255, 186, 75, 72);
        CATEGORIES_COLORS[10].setARGB(255, 156, 215, 163);
        MAIN_COLOR.setARGB(255, 5, 83, 14);
        CENTER_FILL_COLOR.setARGB(255, 210, 255, 215);
    }

    private static void setAntiAliasing(boolean aa) {
        for (Paint paint : CATEGORIES_COLORS) {
            paint.setAntiAlias(aa);
            paint.setFilterBitmap(aa);
        }
        MAIN_COLOR.setAntiAlias(aa);
        MAIN_COLOR.setFilterBitmap(aa);
        CENTER_FILL_COLOR.setAntiAlias(aa);
        CENTER_FILL_COLOR.setFilterBitmap(aa);
    }

    private static Bitmap generate(boolean[] taken, int takenCount) {
        float angleStep = 360f / takenCount;
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawRect((width - stickWidth) / 2, width,
                (width + stickWidth) / 2, height, MAIN_COLOR);
        RectF bounds = new RectF(0, 0, width, width);
        canvas.drawOval(bounds, MAIN_COLOR);

        float currAngle = 0;
        int currType = 0;
        for (int i = 0; i < takenCount; i++) {
            while (currType != taken.length && !taken[currType]) {
                currType++;
            }
            bounds = new RectF(offset, offset, width - offset, width - offset);
            canvas.drawArc(bounds, currAngle, angleStep, true, CATEGORIES_COLORS[currType]);
            currAngle += angleStep;
            currType++;
        }
        bounds = new RectF(width / 4, width / 4, 3 * width / 4, 3 * width / 4);
        canvas.drawOval(bounds, CENTER_FILL_COLOR);

        return image;
    }

    /**
     * Takes widths in dip and recounts all params in px
     *
     * @param widthInDip width in dip
     */
    private static void recount(int widthInDip) {
        MarkerGenerator.widthInDip = widthInDip;
        if (metrics == null) {
            MarkerGenerator.width = DEFAULT_WIDTH;
        } else {
            MarkerGenerator.width = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, widthInDip, metrics);
        }
        height = (int) (width * 1.5);
        offset = (int) (width * 0.04);
        stickWidth = (int) (width * 0.1);
    }

    private static int boolToInt(boolean[] data) {
        if (data.length > 32) {
            throw new IllegalArgumentException("Data length must be < 32 to fit into int variable");
        }
        int result = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i]) {
                result |= 1 << i;
            }
        }
        return result;
    }

    /**
     * Sets width of markers to a given value in dip
     *
     * @param width width in dip
     */
    static void setWidth(int width) {
        recount(width);
    }

    /**
     * This should be called in order to resize markers properly according to device's screen
     *
     * @param metrics DisplayMetrics of current screen
     */
    static void init(DisplayMetrics metrics) {
        MarkerGenerator.metrics = metrics;
        setWidth(widthInDip);
    }

    static Bitmap getIconEcomobile(Resources resources) {
        if (ecomobileIcon == null) {
            Bitmap icon = BitmapFactory.decodeResource(resources, R.mipmap.ecomobile);
            ecomobileIcon = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888); //TODO
            Canvas canvas = new Canvas(ecomobileIcon);
            canvas.drawBitmap(icon, null, new RectF(0, 0, height, width), null);
        }
        return ecomobileIcon;
    }

    static Bitmap getIcon(boolean[] taken) {
        if (taken.length != CATEGORIES_NUMBER) {
            throw new IllegalArgumentException("Size of taken array must be number of categories");
        }
        int takenCount = 0;
        for (boolean type : taken) {
            if (type) {
                takenCount++;
            }
        }
        if (takenCount == 0) {
            throw new IllegalArgumentException("At least one category must be chosen");
        }
        int index = boolToInt(taken);

        if (icons.get(index) == null) {
            icons.put(index, generate(taken, takenCount));
        }
        return icons.get(index);
    }
}