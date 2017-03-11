package com.fastaccess.ui.widgets;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.style.ReplacementSpan;

import com.fastaccess.helper.ViewHelper;

public class RoundBackgroundSpan extends ReplacementSpan {
    private Paint paint = new Paint();
    private Paint empty = new Paint();
    private float radius;
    private int width = -1;

    public RoundBackgroundSpan(int color, float radius) {
        this.paint.setColor(color);
        this.paint.setAntiAlias(true);
        this.radius = radius;
    }

    public int getSize(@NonNull Paint paint, CharSequence charSequence, int i, int i2, FontMetricsInt fontMetricsInt) {
        this.width = Math.round((float) ((int) paint.measureText(charSequence, i, i2)));
        this.width = (int) (((float) this.width) + (this.radius * 4.0f));
        return this.width;
    }

    public void draw(@NonNull Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, @NonNull Paint paint) {
        canvas.drawRoundRect(new RectF(f, (float) i3, ((float) this.width) + f, (float) i5), this.radius, this.radius, this.paint);
        paint.setColor(ViewHelper.generateTextColor(this.paint.getColor()));
        canvas.drawText(charSequence, i, i2, f + (this.radius * 2.0f), (float) i4, paint);
    }
}