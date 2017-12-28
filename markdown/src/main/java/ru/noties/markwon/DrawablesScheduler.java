package ru.noties.markwon;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.noties.markwon.renderer.R;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.AsyncDrawableSpan;

abstract class DrawablesScheduler {

    static void schedule(@NonNull final TextView textView) {

        final List<AsyncDrawable> list = extract(textView);
        if (list.size() > 0) {

            if (textView.getTag(R.id.markwon_drawables_scheduler) == null) {
                final View.OnAttachStateChangeListener listener = new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {

                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        unschedule(textView);
                        v.removeOnAttachStateChangeListener(this);
                        v.setTag(R.id.markwon_drawables_scheduler, null);
                    }
                };
                textView.addOnAttachStateChangeListener(listener);
                textView.setTag(R.id.markwon_drawables_scheduler, listener);
            }

            for (AsyncDrawable drawable : list) {
                drawable.setCallback2(new DrawableCallbackImpl(textView, drawable.getBounds()));
            }
        }
    }

    // must be called when text manually changed in TextView
    static void unschedule(@NonNull TextView view) {
        for (AsyncDrawable drawable : extract(view)) {
            drawable.setCallback2(null);
        }
    }

    private static List<AsyncDrawable> extract(@NonNull TextView view) {

        final List<AsyncDrawable> list;

        final CharSequence cs = view.getText();
        final int length = cs != null
                ? cs.length()
                : 0;

        if (length == 0 || !(cs instanceof Spanned)) {
            //noinspection unchecked
            list = Collections.EMPTY_LIST;
        } else {

            final List<AsyncDrawable> drawables = new ArrayList<>(2);

            final Spanned spanned = (Spanned) cs;
            final AsyncDrawableSpan[] asyncDrawableSpans = spanned.getSpans(0, length, AsyncDrawableSpan.class);
            if (asyncDrawableSpans != null
                    && asyncDrawableSpans.length > 0) {
                for (AsyncDrawableSpan span : asyncDrawableSpans) {
                    drawables.add(span.getDrawable());
                }
            }

            final DynamicDrawableSpan[] dynamicDrawableSpans = spanned.getSpans(0, length, DynamicDrawableSpan.class);
            if (dynamicDrawableSpans != null
                    && dynamicDrawableSpans.length > 0) {
                for (DynamicDrawableSpan span : dynamicDrawableSpans) {
                    final Drawable d = span.getDrawable();
                    if (d != null
                            && d instanceof AsyncDrawable) {
                        drawables.add((AsyncDrawable) d);
                    }
                }
            }

            if (drawables.size() == 0) {
                //noinspection unchecked
                list = Collections.EMPTY_LIST;
            } else {
                list = drawables;
            }
        }

        return list;
    }

    private DrawablesScheduler() {
    }

    private static class DrawableCallbackImpl implements Drawable.Callback {

        private final TextView view;
        private Rect previousBounds;

        DrawableCallbackImpl(TextView view, Rect initialBounds) {
            this.view = view;
            this.previousBounds = new Rect(initialBounds);
        }

        @Override
        public void invalidateDrawable(@NonNull final Drawable who) {

            if (Looper.myLooper() != Looper.getMainLooper()) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        invalidateDrawable(who);
                    }
                });
                return;
            }

            final Rect rect = who.getBounds();

            // okay... the thing is IF we do not change bounds size, normal invalidate would do
            // but if the size has changed, then we need to update the whole layout...

            if (!previousBounds.equals(rect)) {
                // the only method that seems to work when bounds have changed
                view.setText(view.getText());
                previousBounds = new Rect(rect);
            } else {

                view.postInvalidate();
            }
        }

        @Override
        public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
            final long delay = when - SystemClock.uptimeMillis();
            view.postDelayed(what, delay);
        }

        @Override
        public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
            view.removeCallbacks(what);
        }
    }
}
