package com.fastaccess.ui.widgets;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.style.LineBackgroundSpan;

public class DiffLineSpan implements LineBackgroundSpan {
    private static Rect mTmpRect = new Rect();
    private final int color;
    private final int padding;

    public DiffLineSpan(int color, int padding) {
        this.color = color;
        this.padding = padding;
    }

    @Override
    public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start,
                               int end, int lnum) {
        // expand canvas bounds by padding
        Rect clipBounds = c.getClipBounds();
        clipBounds.inset(-padding, 0);
        //c.clipRect(clipBounds, Region.Op.REPLACE);

        final int paintColor = p.getColor();
        p.setColor(color);
        mTmpRect.set(left - padding, top, right + padding, bottom);
        c.drawRect(mTmpRect, p);
        p.setColor(paintColor);
    }
}
