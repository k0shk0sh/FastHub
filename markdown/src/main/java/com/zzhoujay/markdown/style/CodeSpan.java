package com.zzhoujay.markdown.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.style.ReplacementSpan;

/**
 * Created by zhou on 16-7-2.
 * 代码Span
 */
public class CodeSpan extends ReplacementSpan {

    private static final float radius = 10;

    private Drawable drawable;
    private float padding;
    private int width;

    public CodeSpan(int color) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        drawable = d;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        padding = paint.measureText("t");
        width = (int) (paint.measureText(text, start, end) + padding * 2);
        return width;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        drawable.setBounds((int) x, top, (int) x + width, bottom);
        drawable.draw(canvas);
        canvas.drawText(text, start, end, x + padding, y, paint);
    }

}
