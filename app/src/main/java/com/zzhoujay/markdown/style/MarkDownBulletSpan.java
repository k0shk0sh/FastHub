package com.zzhoujay.markdown.style;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.widget.TextView;

import com.zzhoujay.markdown.util.NumberKit;

import java.lang.ref.WeakReference;

/**
 * Created by zhou on 16-6-25.
 * 列表Span
 */
public class MarkDownBulletSpan extends BulletSpan {
    private static final int tab = 40;
    private static final int mGapWidth = 40;
    private static final int BULLET_RADIUS = 6;

    private final boolean mWantColor;
    private final int mColor;
    private final String index;
    private int level = 0;
    private int margin;

    private static Path circleBulletPath = null;
    private static Path rectBulletPath = null;

    private WeakReference<TextView> textViewWeakReference;

    public MarkDownBulletSpan(int l, int color, int pointIndex, TextView textView) {
        super(mGapWidth, color);
        level = l;
        if (pointIndex > 0) {
            if (level == 1) {
                this.index = NumberKit.toRomanNumerals(pointIndex);
            } else if (level >= 2) {
                this.index = NumberKit.toABC(pointIndex - 1);
            } else {
                this.index = pointIndex + "";
            }
        } else {
            index = null;
        }
        mWantColor = true;
        mColor = color;
        textViewWeakReference = new WeakReference<>(textView);
    }

    public MarkDownBulletSpan(int level, int color, int pointIndex) {
        super(mGapWidth, color);
        this.level = level;
        if (pointIndex > 0) {
            if (level == 1) {
                this.index = NumberKit.toRomanNumerals(pointIndex);
            } else if (level >= 2) {
                this.index = NumberKit.toABC(pointIndex - 1);
            } else {
                this.index = pointIndex + "";
            }
        } else {
            index = null;
        }
        mWantColor = true;
        mColor = color;
    }


    @Override
    public int getLeadingMargin(boolean first) {
        TextView textView = textViewWeakReference != null ? textViewWeakReference.get() : null;
        if (index != null && textView != null) {
            margin = (int) (tab + (mGapWidth + textView.getPaint().measureText(index)) * (level + 1));
        } else {
            margin = (2 * BULLET_RADIUS + mGapWidth) * (level + 1) + tab;
        }
        return margin;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout l) {
        if (((Spanned) text).getSpanStart(this) == start) {
            int oldcolor = 0;
            if (mWantColor) {
                oldcolor = p.getColor();
                p.setColor(mColor);
            }
            if (index != null) {
                c.drawText(index + '.', x - p.measureText(index) + margin - mGapWidth, baseline, p);
            } else {
                Paint.Style style = p.getStyle();
                if (level == 1) {
                    p.setStyle(Paint.Style.STROKE);
                } else {
                    p.setStyle(Paint.Style.FILL);
                }

                if (c.isHardwareAccelerated()) {
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

                    c.save();
                    c.translate(x + margin - mGapWidth, (top + bottom) / 2.0f);
                    c.drawPath(path, p);
                    c.restore();
                } else {
                    c.drawCircle(x + margin - mGapWidth, (top + bottom) / 2.0f, BULLET_RADIUS, p);
                }

                p.setStyle(style);
            }
            if (mWantColor) {
                p.setColor(oldcolor);
            }
        }
    }

}
