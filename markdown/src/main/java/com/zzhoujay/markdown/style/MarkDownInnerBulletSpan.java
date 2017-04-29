package com.zzhoujay.markdown.style;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.text.style.ReplacementSpan;

import com.zzhoujay.markdown.util.NumberKit;

/**
 * Created by zhou on 16-7-3.
 * 列表Span
 */
public class MarkDownInnerBulletSpan extends ReplacementSpan {

    private static final int BULLET_RADIUS = 6;
    private static final int tab = 40;
    private static final int gap = 40;

    private final int mColor;
    private final String index;
    private int margin;
    private int level;

    private static Path circleBulletPath = null;
    private static Path rectBulletPath = null;


    public MarkDownInnerBulletSpan(int level, int mColor, int index) {
        this.mColor = mColor;
        this.level = level;
        if (index > 0) {
            if (level == 1) {
                this.index = NumberKit.toRomanNumerals(index) + '.';
            } else if (level >= 2) {
                this.index = NumberKit.toABC(index - 1) + '.';
            } else {
                this.index = index + ".";
            }
        } else {
            this.index = null;
        }
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if (index == null) {
            margin = tab + (gap + BULLET_RADIUS * 2) * (level + 1);
        } else {
            margin = (int) (tab + (gap + paint.measureText(index)) * (level + 1));
        }
        return (int) (margin + paint.measureText(text, start, end));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int oldcolor = paint.getColor();
        paint.setColor(mColor);
        // draw bullet
        if (index != null) {
            canvas.drawText(index, x + tab, y, paint);
        } else {
            Paint.Style style = paint.getStyle();

            if (level == 1) {
                paint.setStyle(Paint.Style.STROKE);
            } else {
                paint.setStyle(Paint.Style.FILL);
            }

            if (canvas.isHardwareAccelerated()) {
                Path path;
                if (level >= 2) {
                    if (rectBulletPath == null) {
                        rectBulletPath = new Path();
                        float w = 1.2f * BULLET_RADIUS;
                        rectBulletPath.addRect(-w, -w, w, w, Path.Direction.CW);
                    }
                    path = rectBulletPath;
                } else {
                    if (circleBulletPath == null) {
                        circleBulletPath = new Path();
                        // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
                        circleBulletPath.addCircle(0.0f, 0.0f, 1.2f * BULLET_RADIUS, Path.Direction.CW);
                    }
                    path = circleBulletPath;
                }

                canvas.save();
                canvas.translate(x + margin - gap, (top + bottom) / 2.0f);
                canvas.drawPath(path, paint);
                canvas.restore();
            } else {
                canvas.drawCircle(x + margin - gap, (top + bottom) / 2.0f, BULLET_RADIUS, paint);
            }
            paint.setStyle(style);
        }
        // drawText
        canvas.drawText(text, start, end, x + margin, y, paint);
        paint.setColor(oldcolor);
    }

}
