package com.zzhoujay.markdown.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.style.LineHeightSpan;
import android.text.style.ReplacementSpan;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhou on 16-7-2.
 * 代码块Span
 */
public class CodeBlockSpan extends ReplacementSpan implements LineHeightSpan {

    private static final float radius = 10;
    private static final int padding = 30;

    private int width;
    private Drawable drawable;
    private int baseLine;
    private int lineHeight;
    private CharSequence[] ls;
    private List<Pair<Integer, Integer>> lines;

    public CodeBlockSpan(int width, int color, CharSequence... lines) {
        this.width = width;
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(radius);
        drawable = g;
        this.ls = lines;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if (fm != null && lines == null) {
            lines = new ArrayList<>();
            for (CharSequence c : ls) {
                lines.addAll(measureTextLine(c, 0, c.length(), paint));
            }
        }
        return width;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        drawable.setBounds((int) x, top, (int) x + width, bottom);
        drawable.draw(canvas);
        int lineNum = 0;
        x = x + padding;
        int i = baseLine + lineHeight / 2 + top;
        for (Pair<Integer, Integer> line : lines) {
            CharSequence t = ls[lineNum];
            canvas.drawText(t, line.first, line.second, x + padding, i, paint);
            if (line.second >= t.length()) {
                lineNum++;
            }
            i += lineHeight;
        }
    }


    private int getTextInLineLen(CharSequence text, int start, int end, Paint paint) {
        int e = start;
        while (paint.measureText(text, start, e) < width - padding * 2) {
            e++;
            if (e > end) {
                break;
            }
        }
        return e - 1;
    }

    private int getTextInLineLenInRange(CharSequence text, int start, int end, int rs, int re, Paint paint) {
        int e = rs;
        if (rs > end) {
            return end;
        }
        while (paint.measureText(text, start, e) < width - padding * 2) {
            e++;
            if (e > end || e > re) {
                break;
            }
        }
        return e - 1;
    }

    private List<Pair<Integer, Integer>> measureTextLine(CharSequence text, int start, int end, Paint paint) {
        List<Pair<Integer, Integer>> lines = new ArrayList<>();
        int l = getTextInLineLen(text, start, end, paint);
        int count = l;
        lines.add(new Pair<>(start, l));
        while (l < end) {
            int temp = l;
            l = getTextInLineLenInRange(text, l, end, l + count - 4, l + count + 4, paint);
            count = l - temp;
            lines.add(new Pair<>(temp, l));
        }
        return lines;
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
        int num = lines.size();
        lineHeight = fm.bottom - fm.top;
        baseLine = -fm.top;
        fm.ascent = fm.top;
        fm.bottom += num * lineHeight;
        fm.descent = fm.bottom;
    }


}
