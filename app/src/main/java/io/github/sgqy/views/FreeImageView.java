package io.github.sgqy.views;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import io.github.sgqy.utils.PointConvert;

/**
 * TODO: document your custom view class.
 */
public class FreeImageView extends ImageView {

    Matrix prevMatrix = new Matrix();
    PointF prevMiddlePoint = new PointF();
    float prevDistance = 0;
    PointF lastTouchedPoint = new PointF();
    boolean wasMultiTouch = false;
    MatrixModifiedCallback matrixModifiedCallback;
    public FreeImageView(Context context) {
        super(context);
    }

    public FreeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FreeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PointF getLastTouchedPointOnScreen() {
        return lastTouchedPoint;
    }

    public PointF getRealImagePositionFromLastTouchedScreen() {
        return PointConvert.FromScreenToRealImage(getLastTouchedPointOnScreen(), getImageMatrix());
    }

    public PointF getScreenPositionFromRealImage(PointF pos) {
        return PointConvert.FromRealImageToScreen(pos, getImageMatrix());
    }

    public PointF getRealImagePositionFromProp(PointF prop) {
        Point size = getBitmapSize();
        float relX = size.x * prop.x;
        float relY = size.y * prop.y;
        return new PointF(relX, relY);
    }

    public PointF getScreenPositionFromPropOfRealImage(PointF prop) {
        return getScreenPositionFromRealImage(getRealImagePositionFromProp(prop));
    }

    public PointF getPropOfLastTouchedAndRealImageSize() {
        PointF relativePosition = getRealImagePositionFromLastTouchedScreen();
        Point size = getBitmapSize();
        return new PointF(relativePosition.x / size.x, relativePosition.y / size.y);
    }

    Point getBitmapSize() {
        Drawable drawable = getDrawable();
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        return new Point(w, h);
    }

    PointF getMiddlePoint(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            return new PointF(
                    (event.getX(0) + event.getX(1)) / 2,
                    (event.getY(0) + event.getY(1)) / 2
            );
        }
        return new PointF(event.getX(), event.getY());
    }

    float getDistance(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float dx = event.getX(0) - event.getX(1);
            float dy = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(dx * dx + dy * dy);
        }
        return 1;
    }

    public void setMatrixModifiedCallback(MatrixModifiedCallback theirInterface) {
        this.matrixModifiedCallback = theirInterface;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    wasMultiTouch = true;
                    prevMatrix.set(getImageMatrix());
                    prevMiddlePoint = getMiddlePoint(event);
                    prevDistance = getDistance(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2) {
                    Matrix matrix = new Matrix();
                    PointF currMiddlePoint = getMiddlePoint(event);
                    float currDistance = getDistance(event);
                    float scale = 1;
                    try {
                        scale = currDistance / prevDistance;
                    } catch (Exception e) {

                    }

                    matrix.set(prevMatrix);
                    matrix.postTranslate(
                            currMiddlePoint.x - prevMiddlePoint.x,
                            currMiddlePoint.y - prevMiddlePoint.y
                    );
                    matrix.postScale(scale, scale, prevMiddlePoint.x, prevMiddlePoint.y);
                    setImageMatrix(matrix);
                    matrixModifiedCallback.onMatrixModified(matrix);
                }
                break;
            case MotionEvent.ACTION_UP:
                lastTouchedPoint.set(event.getX(), event.getY());
                if (wasMultiTouch) {
                    wasMultiTouch = false;
                } else {
                    callOnClick();
                }
                break;
        }

        return true;
    }
}
