package com.fastaccess.ui.widgets;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.style.ReplacementSpan;

public class RoundBackgroundSpan extends ReplacementSpan {
    private Paint mPaint = new Paint();
    private float mRadius;
    private RectF mRectF = new RectF();
    private int mWidth = -1;

    public RoundBackgroundSpan(int i, float f) {
        this.mPaint.setColor(i);
        this.mPaint.setAntiAlias(true);
        this.mRadius = f;
    }

    public int getSize(@NonNull Paint paint, CharSequence charSequence, int i, int i2, FontMetricsInt fontMetricsInt) {
        this.mWidth = Math.round((float) ((int) paint.measureText(charSequence, i, i2)));
        this.mWidth = (int) (((float) this.mWidth) + (this.mRadius * 4.0f));
        return this.mWidth;
    }

    public void draw(@NonNull Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, @NonNull Paint paint) {
        canvas.drawRoundRect(new RectF(f, (float) i3, ((float) this.mWidth) + f, (float) i5), this.mRadius, this.mRadius, this.mPaint);
        canvas.drawText(charSequence, i, i2, f + (this.mRadius * 2.0f), (float) i4, paint);
    }
}