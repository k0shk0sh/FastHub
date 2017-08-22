package com.fastaccess.ui.widgets;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.LineBackgroundSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.TypefaceSpan;

import com.fastaccess.App;
import com.fastaccess.R;
import com.fastaccess.helper.InputHelper;

import java.util.regex.Pattern;

public class DiffLineSpan extends MetricAffectingSpan implements LineBackgroundSpan {
    private Rect rect = new Rect();
    private final int color;
    public static Pattern HUNK_TITLE = Pattern.compile("^.*-([0-9]+)(?:,([0-9]+))? \\+([0-9]+)(?:,([0-9]+))?.*$");

    private DiffLineSpan(int color) {
        this.color = color;
    }

    @Override public void updateMeasureState(TextPaint paint) {
        apply(paint);
    }

    @Override public void updateDrawState(TextPaint paint) {
        apply(paint);
    }

    private void apply(TextPaint paint) {
        paint.setTypeface(Typeface.MONOSPACE);
    }

    @Override public void drawBackground(Canvas c, Paint p, int left, int right, int top, int baseline, int bottom, CharSequence text, int start,
                                         int end, int lnum) {
        Paint.Style style = p.getStyle();
        int color = p.getColor();
        p.setStyle(Paint.Style.FILL);
        p.setColor(this.color);
        rect.set(left, top, right, bottom);
        c.drawRect(rect, p);
        p.setColor(color);
        p.setStyle(style);
    }

    @NonNull public static SpannableStringBuilder getSpannable(@Nullable String text, @ColorInt int patchAdditionColor,
                                                               @ColorInt int patchDeletionColor, @ColorInt int patchRefColor) {
        return getSpannable(text, patchAdditionColor, patchDeletionColor, patchRefColor, false);
    }

    @NonNull public static SpannableStringBuilder getSpannable(@Nullable String text, @ColorInt int patchAdditionColor,
                                                               @ColorInt int patchDeletionColor, @ColorInt int patchRefColor,
                                                               boolean truncate) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (!InputHelper.isEmpty(text)) {
            String[] split = text.split("\\r?\\n|\\r");
            if (split.length > 0) {
                int lines = split.length;
                int index = -1;
                for (int i = 0; i < lines; i++) {
                    if (truncate && (lines - i) > 2) continue;
                    String token = split[i];
                    if (i < (lines - 1)) {
                        token = token.concat("\n");
                    }
                    char firstChar = token.charAt(0);
                    int color = Color.TRANSPARENT;
                    if (token.startsWith("@@")) {
                        color = patchRefColor;
                    } else if (firstChar == '+') {
                        color = patchAdditionColor;
                    } else if (firstChar == '-') {
                        color = patchDeletionColor;
                    }
                    index = token.indexOf("\\ No newline at end of file");
                    if (index != -1) {
                        token = token.replace("\\ No newline at end of file", "");
                    }
                    SpannableString spannableDiff = new SpannableString(token);
                    if (color != Color.TRANSPARENT) {
                        DiffLineSpan span = new DiffLineSpan(color);
                        spannableDiff.setSpan(span, 0, token.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    builder.append(spannableDiff);
                }
                if (index != -1) {
                    builder.insert(builder.length() - 1,
                            SpannableBuilder.builder().append(ContextCompat.getDrawable(App.getInstance(), R.drawable.ic_newline)));
                }
            }
        }
        builder.setSpan(new TypefaceSpan("monospace"), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

}
