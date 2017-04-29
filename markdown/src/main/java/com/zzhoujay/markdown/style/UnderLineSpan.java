package com.zzhoujay.markdown.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.LineHeightSpan;
import android.text.style.ReplacementSpan;

/**
 * Created by zhou on 16-7-2.
 */
public class UnderLineSpan extends ReplacementSpan implements LineHeightSpan {

    private int height;
    private int width;
    private Drawable drawable;

    public UnderLineSpan(Drawable drawable, int width, int height) {
        this.height = height;
        this.width = width;
        this.drawable = drawable;
    }


    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return (int) paint.measureText(text, start, end);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        drawable.setBounds((int) x, bottom - height, (int) x + width, bottom);
        drawable.draw(canvas);
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
        fm.top /= 3;
        fm.ascent /= 3;
        fm.bottom /= 3;
        fm.descent /= 3;
    }
}
