package in.uncod.android.bypass.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.style.ReplacementSpan;

/**
 * Draws a line across the screen.
 */
public class HorizontalLineSpan extends ReplacementSpan {

    private int mLineHeight;
    private Paint mPaint = new Paint();
    private int mTopBottomPadding;

    public HorizontalLineSpan(int color, int lineHeight, int topBottomPadding) {
        this.mPaint.setColor(color);
        this.mLineHeight = lineHeight;
        this.mTopBottomPadding = topBottomPadding;
    }

    @Override public int getSize(@NonNull Paint paint, CharSequence charSequence, int start, int end, Paint.FontMetricsInt fontMetricsInt) {
        if (fontMetricsInt != null) {
            fontMetricsInt.ascent = (-this.mLineHeight) - this.mTopBottomPadding;
            fontMetricsInt.descent = 0;
            fontMetricsInt.top = fontMetricsInt.ascent;
            fontMetricsInt.bottom = 0;
        }
        return Integer.MAX_VALUE;
    }

    @Override public void draw(@NonNull Canvas canvas, CharSequence charSequence, int i, int i2,
                               float f, int i3, int i4, int i5, @NonNull Paint paint) {
        int i6 = (i3 + i5) / 2;
        int i7 = this.mLineHeight / 2;
        canvas.drawRect(f, (float) (i6 - i7), 2.14748365E9f, (float) (i6 + i7), this.mPaint);
    }
}
