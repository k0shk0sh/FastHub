package com.fastaccess.provider.timeline.handler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.TextPaint;
import android.text.style.LeadingMarginSpan;
import android.text.style.LineBackgroundSpan;
import android.text.style.MetricAffectingSpan;

public class CodeBackgroundRoundedSpan extends MetricAffectingSpan implements LeadingMarginSpan, LineBackgroundSpan {
    private final int color;

    private final RectF rect = new RectF();

    CodeBackgroundRoundedSpan(int color) {
        this.color = color;
    }

    @Override public void updateMeasureState(TextPaint paint) {
        apply(paint);
    }

    @Override public void updateDrawState(TextPaint paint) {
        apply(paint);
    }

    private void apply(TextPaint paint) {
        paint.setTypeface(Typeface.MONOSPACE);
    }

    @Override public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom,
                                         CharSequence text, int start, int end, int lnum) {
        Paint.Style style = p.getStyle();
        int color = p.getColor();
        p.setStyle(Paint.Style.FILL);
        p.setColor(this.color);
        rect.set(left, top, right, bottom);
        c.drawRect(rect, p);
        p.setColor(color);
        p.setStyle(style);
    }

    @Override public int getLeadingMargin(boolean first) {
        return 30;
    }

    @Override public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom,
                                            CharSequence text, int start, int end, boolean first, Layout layout) {}
}