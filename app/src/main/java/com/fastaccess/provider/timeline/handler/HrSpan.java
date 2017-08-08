package com.fastaccess.provider.timeline.handler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.style.LineHeightSpan;
import android.text.style.ReplacementSpan;

public class HrSpan extends ReplacementSpan implements LineHeightSpan {

    private final int width;
    private final int color;

    HrSpan(int color, int width) {
        this.color = color;
        this.width = width;
        Drawable drawable = new ColorDrawable(color);
    }

    @Override public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return (int) paint.measureText(text, start, end);
    }

    @Override public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top,
                               int y, int bottom, @NonNull Paint paint) {
        final int currentColor = paint.getColor();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        int height = 10;
        canvas.drawRect(new Rect(0, bottom - height, (int) x + width, bottom), paint);
        paint.setColor(currentColor);
    }

    @Override public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
        fm.top /= 3;
        fm.ascent /= 3;
        fm.bottom /= 3;
        fm.descent /= 3;
    }
}