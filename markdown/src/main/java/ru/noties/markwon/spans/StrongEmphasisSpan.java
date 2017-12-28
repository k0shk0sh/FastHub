package ru.noties.markwon.spans;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class StrongEmphasisSpan extends MetricAffectingSpan {

    @Override
    public void updateMeasureState(TextPaint p) {
        p.setFakeBoldText(true);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setFakeBoldText(true);
    }
}
