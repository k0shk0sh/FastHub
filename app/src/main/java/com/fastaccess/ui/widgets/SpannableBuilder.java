package com.fastaccess.ui.widgets;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.View;

import com.fastaccess.helper.InputHelper;

import static android.graphics.Typeface.BOLD;

/**
 * Created by Kosh on 15 Nov 2016, 9:26 PM
 */

public class SpannableBuilder extends SpannableStringBuilder {

    private SpannableBuilder() {}

    public static SpannableBuilder builder() {
        return new SpannableBuilder();
    }

    public SpannableBuilder append(final CharSequence text, final Object span) {
        if (!InputHelper.isEmpty(text)) {
            append(text);
            if (span != null) {
                final int length = length();
                setSpan(span, length - text.length(), length, SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return this;
    }

    @Override public SpannableBuilder append(char text) {
        if (text != 0) super.append(text);
        return this;
    }

    @Override public SpannableBuilder append(CharSequence text) {
        if (text != null) super.append(text);
        return this;
    }

    public SpannableBuilder append(Object span) {
        setSpan(span, length() - 1, length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        return this;
    }

    public SpannableBuilder append(Drawable drawable) {
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            append(' ', new ImageSpan(drawable));
        }
        return this;
    }

    public SpannableBuilder append(final char text, final Object span) {
        append(text);
        if (!InputHelper.isEmpty(span)) {
            final int length = length();
            setSpan(span, length - 1, length, SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return this;
    }

    public SpannableBuilder bold(CharSequence text, final Object span) {
        if (!InputHelper.isEmpty(text)) {
            text = SpannableBuilder.builder().bold(text);
            append(text, span);
        }
        return this;
    }

    public SpannableBuilder bold(final CharSequence text) {
        if (!InputHelper.isEmpty(text)) return append(text, new StyleSpan(BOLD));
        return this;
    }

    public SpannableBuilder background(final CharSequence text, final int color) {
        if (!InputHelper.isEmpty(text))
            return append(text, new BackgroundColorSpan(color));
        return this;
    }

    public SpannableBuilder foreground(final CharSequence text, @ColorInt int color) {
        if (!InputHelper.isEmpty(text)) return append(text, new ForegroundColorSpan(color));
        return this;
    }

    public SpannableBuilder foreground(final char text, @ColorInt int color) {
        return append(text, new ForegroundColorSpan(color));
    }

    public SpannableBuilder url(final CharSequence text, final View.OnClickListener listener) {
        if (!InputHelper.isEmpty(text))
            return append(text, new URLSpan(text.toString()) {
                @Override
                public void onClick(View widget) {
                    listener.onClick(widget);
                }
            });
        return this;
    }

    public SpannableBuilder url(final CharSequence text) {
        if (!InputHelper.isEmpty(text)) return append(text, new URLSpan(text.toString()));
        return this;
    }

    public SpannableBuilder clickable(final CharSequence text, final View.OnClickListener listener) {
        if (!InputHelper.isEmpty(text)) return append(text, new ClickableSpan() {
            @Override public void updateDrawState(TextPaint ds) {
                ds.setColor(ds.linkColor);
                ds.setUnderlineText(false);
            }

            @Override public void onClick(View widget) {
                listener.onClick(widget);
            }
        });
        return this;
    }

}

