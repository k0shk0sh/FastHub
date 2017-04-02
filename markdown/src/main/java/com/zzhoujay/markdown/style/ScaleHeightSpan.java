package com.zzhoujay.markdown.style;

import android.graphics.Paint;
import android.text.style.LineHeightSpan;

/**
 * Created by zhou on 16-7-2.
 * ScaleHeightSpan
 */
public class ScaleHeightSpan implements LineHeightSpan {

    private float scale;

    public ScaleHeightSpan(float scale) {
        this.scale = scale;
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
        fm.ascent *= scale;
        fm.top *= scale;
        fm.descent *= scale;
        fm.bottom *= scale;
    }

}
