package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class ThematicBreakSpan implements LeadingMarginSpan {

    private final SpannableTheme theme;
    private final Rect rect = ObjectsPool.rect();
    private final Paint paint = ObjectsPool.paint();

    public ThematicBreakSpan(@NonNull SpannableTheme theme) {
        this.theme = theme;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return 0;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        final int middle = top + ((bottom - top) / 2);

        paint.set(p);
        theme.applyThematicBreakStyle(paint);

        final int height = (int) (paint.getStrokeWidth() + .5F);
        final int halfHeight = (int) (height / 2.F + .5F);

        final int left;
        final int right;
        if (dir > 0) {
            left = x;
            right = c.getWidth();
        } else {
            left = x - c.getWidth();
            right = x;
        }

        rect.set(left, middle - halfHeight, right, middle + halfHeight);
        c.drawRect(rect, paint);
    }
}
