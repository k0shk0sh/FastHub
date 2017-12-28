package ru.noties.markwon.spans;

import android.text.Spanned;

abstract class LeadingMarginUtils {

    static boolean selfStart(int start, CharSequence text, Object span) {
        return text instanceof Spanned && ((Spanned) text).getSpanStart(span) == start;
    }

    static boolean selfEnd(int end, CharSequence text, Object span) {
        return text instanceof Spanned && ((Spanned) text).getSpanEnd(span) == end;
    }

    private LeadingMarginUtils() {
    }
}
