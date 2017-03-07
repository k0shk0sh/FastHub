package in.uncod.android.bypass.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class QuoteStyle implements LeadingMarginSpan {
    private final int mColor;
    private final int mGapWidth;
    private final int mStripeWidth;

    public QuoteStyle(int strokeWidth) {
        this.mStripeWidth = strokeWidth;
        this.mGapWidth = strokeWidth * 2;
        this.mColor = -12627531;
    }

    public QuoteStyle(int strokeWidth, int color) {
        this.mStripeWidth = strokeWidth;
        this.mGapWidth = strokeWidth * 2;
        this.mColor = color;
    }

    public int getColor() {
        return this.mColor;
    }

    @Override public int getLeadingMargin(boolean z) {
        return this.mStripeWidth + this.mGapWidth;
    }

    @Override public void drawLeadingMargin(Canvas canvas, Paint paint, int i, int i2, int i3, int i4, int i5,
                                            CharSequence charSequence, int i6, int i7, boolean z, Layout layout) {
        Style style = paint.getStyle();
        int color = paint.getColor();
        paint.setStyle(Style.FILL);
        paint.setColor(this.mColor);
        canvas.drawRect((float) i, (float) i3, (float) ((this.mStripeWidth * i2) + i), (float) i5, paint);
        paint.setStyle(style);
        paint.setColor(color);
    }
}