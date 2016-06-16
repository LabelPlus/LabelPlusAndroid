package io.github.sgqy.utils;

import android.graphics.Matrix;
import android.graphics.PointF;

/**
 * Created by aya on 2016/06/12.
 */
public class PointConvert {
    public static PointF FromRealImageToScreen(PointF src, Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        float x = src.x * values[0] + values[2];
        float y = src.y * values[4] + values[5];
        return new PointF(x, y);
    }

    public static PointF FromScreenToRealImage(PointF src, Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        float x = (src.x - values[2]) / values[0];
        float y = (src.y - values[5]) / values[4];
        return new PointF(x, y);
    }
}
