package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @since 1.0.1
 */
@SuppressWarnings("WeakerAccess")
public class TaskListDrawable extends Drawable {

    // represent ratios (not exact coordinates)
    private static final Point POINT_0 = new Point(2.75F / 18, 8.25F / 18);
    private static final Point POINT_1 = new Point(7.F / 18, 12.5F / 18);
    private static final Point POINT_2 = new Point(15.25F / 18, 4.75F / 18);

    private final int checkedFillColor;
    private final int normalOutlineColor;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rectF = new RectF();

    private final Paint checkMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path checkMarkPath = new Path();

    private boolean isChecked;

    // unfortunately we cannot rely on TextView to be LAYER_TYPE_SOFTWARE
    // if we could we would draw our checkMarkPath with PorterDuff.CLEAR
    public TaskListDrawable(@ColorInt int checkedFillColor, @ColorInt int normalOutlineColor, @ColorInt int checkMarkColor) {
        this.checkedFillColor = checkedFillColor;
        this.normalOutlineColor = normalOutlineColor;

        checkMarkPaint.setColor(checkMarkColor);
        checkMarkPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        // we should exclude stroke with from final bounds (half of the strokeWidth from all sides)

        // we should have square shape
        final float min = Math.min(bounds.width(), bounds.height());
        final float stroke = min / 8;

        final float side = min - stroke;
        rectF.set(0, 0, side, side);

        paint.setStrokeWidth(stroke);
        checkMarkPaint.setStrokeWidth(stroke);

        checkMarkPath.reset();

        POINT_0.moveTo(checkMarkPath, side);
        POINT_1.lineTo(checkMarkPath, side);
        POINT_2.lineTo(checkMarkPath, side);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        final Paint.Style style;
        final int color;

        if (isChecked) {
            style = Paint.Style.FILL_AND_STROKE;
            color = checkedFillColor;
        } else {
            style = Paint.Style.STROKE;
            color = normalOutlineColor;
        }
        paint.setStyle(style);
        paint.setColor(color);

        final Rect bounds = getBounds();

        final float left = (bounds.width() - rectF.width()) / 2;
        final float top = (bounds.height() - rectF.height()) / 2;

        final float radius = rectF.width() / 8;

        final int save = canvas.save();
        try {

            canvas.translate(left, top);

            canvas.drawRoundRect(rectF, radius, radius, paint);

            if (isChecked) {
                canvas.drawPath(checkMarkPath, checkMarkPaint);
            }
        } finally {
            canvas.restoreToCount(save);
        }
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public boolean isStateful() {
        return true;
    }

    @Override
    protected boolean onStateChange(int[] state) {

        final boolean checked;

        final int length = state != null
                ? state.length
                : 0;

        if (length > 0) {

            boolean inner = false;

            for (int i = 0; i < length; i++) {
                if (android.R.attr.state_checked == state[i]) {
                    inner = true;
                    break;
                }
            }
            checked = inner;
        } else {
            checked = false;
        }

        final boolean result = checked != isChecked;
        if (result) {
            invalidateSelf();
            isChecked = checked;
        }

        return result;
    }

    private static class Point {

        final float x;
        final float y;

        Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        void moveTo(@NonNull Path path, float side) {
            path.moveTo(side * x, side * y);
        }

        void lineTo(@NonNull Path path, float side) {
            path.lineTo(side * x, side * y);
        }
    }
}
