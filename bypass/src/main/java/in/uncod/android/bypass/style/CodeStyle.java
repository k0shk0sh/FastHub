package in.uncod.android.bypass.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.LineBackgroundSpan;

public class CodeStyle implements LineBackgroundSpan {
    private int color;
    private RectF rect = new RectF();

    public CodeStyle(int color) {
        this.color = color;
    }

    @Override public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline,
                                         int bottom, CharSequence text, int start, int end, int lnum) {
        final int textWidth = Math.round(p.measureText(text, start, end));
        final int paintColor = p.getColor();
        int mPadding = 16;
        rect.set(left - mPadding, top - (lnum == 0 ? mPadding / 2 : -(mPadding / 2)), left + textWidth + mPadding, bottom + mPadding / 2);
        p.setColor(color);
        c.drawRect(rect, p);
        p.setColor(paintColor);
    }
}