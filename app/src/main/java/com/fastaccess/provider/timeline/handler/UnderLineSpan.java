package com.fastaccess.provider.timeline.handler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.style.LineHeightSpan;
import android.text.style.ReplacementSpan;

public class UnderLineSpan extends ReplacementSpan implements LineHeightSpan {

    private final int height = 5;
    private int width;
    private final Drawable drawable;

    UnderLineSpan(int color, int width) {
        this.width = width;
        this.drawable = new ColorDrawable(color);
    }

    @Override public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return (int) paint.measureText(text, start, end);
    }

    @Override public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top,
                               int y, int bottom, @NonNull Paint paint) {
        drawable.setBounds((int) x, bottom - height, (int) x + width, bottom);
        drawable.draw(canvas);
    }

    @Override public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
        fm.top /= 3;
        fm.ascent /= 3;
        fm.bottom /= 3;
        fm.descent /= 3;
    }
}