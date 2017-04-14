package com.fastaccess.ui.widgets;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.style.ReplacementSpan;

import com.fastaccess.helper.ViewHelper;

public class RoundBackgroundSpan extends ReplacementSpan {
    private final int color;
    private int width = -1;
    private final RectF rectF;


    public RoundBackgroundSpan(@ColorInt int color) {
        this.color = color;
        rectF = new RectF();
    }

    public int getSize(@NonNull Paint paint, CharSequence charSequence, int start, int end, FontMetricsInt fontMetricsInt) {
        this.width = Math.round(paint.measureText(charSequence, start, end));
        this.width = (int) (this.width + (5 * 4.0f));
        return this.width;
    }

    public void draw(@NonNull Canvas canvas, @NonNull CharSequence charSequence,
                     int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        paint.setColor(color);
        rectF.set(x, top, width + x, bottom);
        canvas.drawRoundRect(rectF, 5, 5, paint);
        paint.setColor(ViewHelper.generateTextColor(color));
        canvas.drawText(charSequence, start, end, x + (5 * 2.0f), (float) y, paint);

    }
}