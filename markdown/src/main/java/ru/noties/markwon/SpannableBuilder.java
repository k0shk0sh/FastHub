package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

/**
 * This class is used to _revert_ order of applied spans. Original SpannableStringBuilder
 * is using an array to store all the information about spans. So, a span that is added first
 * will be drawn first, which leads to subtle bugs (spans receive wrong `x` values when
 * requested to draw itself)
 *
 * @since 1.0.1
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class SpannableBuilder {

    // do not implement CharSequence (or any of Spanned interfaces)

    // we will be using SpannableStringBuilder anyway as a backing store
    // as it has tight connection with system (implements some hidden methods, etc)
    private final SpannableStringBuilder builder;

    // actually we might be just using ArrayList
    private final Deque<Span> spans = new ArrayDeque<>(8);

    public SpannableBuilder() {
        this("");
    }

    public SpannableBuilder(@NonNull CharSequence cs) {
        this.builder = new SpannableStringBuilderImpl(cs.toString());
        copySpans(0, cs);
    }

    /**
     * Additional method that takes a String, which is proven to NOT contain any spans
     *
     * @param text String to append
     * @return this instance
     */
    @NonNull
    public SpannableBuilder append(@NonNull String text) {
        builder.append(text);
        return this;
    }

    @NonNull
    public SpannableBuilder append(char c) {
        builder.append(c);
        return this;
    }

    @NonNull
    public SpannableBuilder append(@NonNull CharSequence cs) {

        copySpans(length(), cs);

        builder.append(cs.toString());

        return this;
    }

    @NonNull
    public SpannableBuilder append(@NonNull CharSequence cs, @NonNull Object span) {
        final int length = length();
        append(cs);
        setSpan(span, length);
        return this;
    }

    @NonNull
    public SpannableBuilder append(@NonNull CharSequence cs, @NonNull Object span, int flags) {
        final int length = length();
        append(cs);
        setSpan(span, length, length(), flags);
        return this;
    }

    @NonNull
    public SpannableBuilder setSpan(@NonNull Object span, int start) {
        return setSpan(span, start, length());
    }

    @NonNull
    public SpannableBuilder setSpan(@NonNull Object span, int start, int end) {
        return setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @NonNull
    public SpannableBuilder setSpan(@NonNull Object span, int start, int end, int flags) {
        spans.push(new Span(span, start, end, flags));
        return this;
    }

    public int length() {
        return builder.length();
    }

    public char charAt(int index) {
        return builder.charAt(index);
    }

    public char lastChar() {
        return builder.charAt(length() - 1);
    }

    @NonNull
    public CharSequence removeFromEnd(int start) {

        // this method is not intended to be used by clients
        // it's a workaround to support tables

        final int end = length();

        // as we do not expose builder and do no apply spans to it, we are safe to NOT to convert to String
        final SpannableStringBuilderImpl impl = new SpannableStringBuilderImpl(builder.subSequence(start, end));

        final Iterator<Span> iterator = spans.iterator();

        Span span;

        while (iterator.hasNext() && ((span = iterator.next())) != null) {
            if (span.start >= start && span.end <= end) {
                impl.setSpan(span.what, span.start - start, span.end - start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                iterator.remove();
            }
        }

        builder.replace(start, end, "");

        return impl;
    }

    @Override
    @NonNull
    public String toString() {
        return builder.toString();
    }

    @NonNull
    public CharSequence text() {

        // okay, in order to not allow external modification and keep our spans order
        // we should not return our builder
        //
        // plus, if this method was called -> all spans would be applied, which potentially
        // breaks the order that we intend to use
        // so, we will defensively copy builder

        // as we do not expose builder and do no apply spans to it, we are safe to NOT to convert to String
        final SpannableStringBuilderImpl impl = new SpannableStringBuilderImpl(builder);

        for (Span span : spans) {
            impl.setSpan(span.what, span.start, span.end, span.flags);
        }

        // now, let's remove trailing newLines (so small amounts of text are displayed correctly)
        // @since 1.0.2

        final int length = impl.length();
        if (length > 0) {
            int amount = 0;
            for (int i = length - 1; i >=0 ; i--) {
                if (Character.isWhitespace(impl.charAt(i))) {
                    amount += 1;
                } else {
                    break;
                }
            }
            if (amount > 0) {
                impl.replace(length - amount, length, "");
            }
        }

        return impl;
    }

    private void copySpans(final int index, @Nullable CharSequence cs) {

        // we must identify already reversed Spanned...
        // and (!) iterate backwards when adding (to preserve order)

        if (cs instanceof Spanned) {

            final Spanned spanned = (Spanned) cs;
            final boolean reverse = spanned instanceof SpannedReversed;

            final Object[] spans = spanned.getSpans(0, spanned.length(), Object.class);

            iterate(reverse, spans, new Action() {
                @Override
                public void apply(Object o) {
                    setSpan(
                            o,
                            index + spanned.getSpanStart(o),
                            index + spanned.getSpanEnd(o),
                            spanned.getSpanFlags(o)
                    );
                }
            });
        }
    }

    static class Span {

        final Object what;
        int start;
        int end;
        final int flags;

        Span(@NonNull Object what, int start, int end, int flags) {
            this.what = what;
            this.start = start;
            this.end = end;
            this.flags = flags;
        }
    }

    private interface Action {
        void apply(Object o);
    }

    private static void iterate(boolean reverse, @Nullable Object[] array, @NonNull Action action) {
        final int length = array != null
                ? array.length
                : 0;
        if (length > 0) {
            if (reverse) {
                for (int i = length - 1; i >= 0; i--) {
                    action.apply(array[i]);
                }
            } else {
                for (int i = 0; i < length; i++) {
                    action.apply(array[i]);
                }
            }
        }
    }
}
