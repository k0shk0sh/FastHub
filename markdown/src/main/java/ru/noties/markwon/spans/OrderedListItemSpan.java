package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

public class OrderedListItemSpan implements LeadingMarginSpan {

    private final SpannableTheme theme;
    private final String number;

    public OrderedListItemSpan(
            @NonNull SpannableTheme theme,
            @NonNull String number
    ) {
        this.theme = theme;
        this.number = number;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return theme.getBlockMargin();
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {

        // if there was a line break, we don't need to draw anything
        if (!first
                || !LeadingMarginUtils.selfStart(start, text, this)) {
            return;
        }

        theme.applyListItemStyle(p);

        final int width = theme.getBlockMargin();
        final int numberWidth = (int) (p.measureText(number) + .5F);

        final int left;
        if (dir > 0) {
            left = x + (width * dir) - numberWidth;
        } else {
            left = x + (width * dir) + (width - numberWidth);
        }

        final float numberY = CanvasUtils.textCenterY(top, bottom, p);

        c.drawText(number, left, numberY, p);
    }
}
