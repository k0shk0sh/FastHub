package ru.noties.markwon.spans;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class EmphasisSpan extends MetricAffectingSpan {

    @Override
    public void updateMeasureState(TextPaint p) {
        p.setTextSkewX(-0.25F);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTextSkewX(-0.25F);
    }
}
