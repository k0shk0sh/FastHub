package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.TextPaint;
import android.text.style.LeadingMarginSpan;
import android.text.style.MetricAffectingSpan;

public class CodeSpan extends MetricAffectingSpan implements LeadingMarginSpan {

    private final SpannableTheme theme;
    private final Rect rect = ObjectsPool.rect();
    private final Paint paint = ObjectsPool.paint();

    private final boolean multiline;

    public CodeSpan(@NonNull SpannableTheme theme, boolean multiline) {
        this.theme = theme;
        this.multiline = multiline;
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        apply(p);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        apply(ds);
        if (!multiline) {
            ds.bgColor = theme.getCodeBackgroundColor(ds);
        }
    }

    private void apply(TextPaint p) {
        theme.applyCodeTextStyle(p);
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return multiline ? theme.getCodeMultilineMargin() : 0;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        if (multiline) {

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(theme.getCodeBackgroundColor(p));

            final int left;
            final int right;
            if (dir > 0) {
                left = x;
                right = c.getWidth();
            } else {
                left = x - c.getWidth();
                right = x;
            }

            rect.set(left, top, right, bottom);

            c.drawRect(rect, paint);
        }
    }
}
