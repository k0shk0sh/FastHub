package com.zzhoujay.markdown.util;

import android.graphics.Paint;

/**
 * Created by zhou on 16-7-3.
 */
public class FontKit {

    public static void scale(Paint.FontMetricsInt fm, float scale) {
        fm.top *= scale;
        fm.bottom *= scale;
        fm.ascent *= scale;
        fm.descent *= scale;
        fm.leading *= scale;
    }

    public static void scaleTo(Paint.FontMetricsInt from, Paint.FontMetricsInt to, float scale) {
        to.top = (int) (from.top * scale);
        to.bottom = (int) (from.bottom * scale);
        to.ascent = (int) (from.ascent * scale);
        to.descent = (int) (from.descent * scale);
        to.leading = (int) (from.leading * scale);
    }
}
