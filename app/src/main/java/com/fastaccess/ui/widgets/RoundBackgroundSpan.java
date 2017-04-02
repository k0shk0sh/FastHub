package com.fastaccess.ui.widgets;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.style.ReplacementSpan;

import com.fastaccess.helper.ViewHelper;

public class RoundBackgroundSpan extends ReplacementSpan {
    @NonNull private Paint paint = new Paint();
    private final float radius = 5;

    public RoundBackgroundSpan(int color) {
        super();
        this.paint.setColor(color);
        this.paint.setAntiAlias(true);
    }

    public int getSize(@NonNull Paint paint, CharSequence charSequence, int start, int end, FontMetricsInt fontMetricsInt) {
        return (int) (10 + paint.measureText(charSequence.subSequence(start, end).toString()) + 10);

    }

    @Override public void draw(@NonNull Canvas canvas, @NonNull CharSequence charSequence, int start, int end, float x,
                               int top, int y, int bottom, @NonNull Paint paint) {
        final float width = paint.measureText(charSequence.subSequence(start, end).toString());
        RectF rectF = new RectF(x, top + 10, x + width + 2 * 10, bottom + 5);
        canvas.drawRoundRect(rectF, this.radius, this.radius, this.paint);
        paint.setColor(ViewHelper.generateTextColor(this.paint.getColor()));
        canvas.drawText(charSequence, start, end, x + 10, y + 5, paint);
    }
}