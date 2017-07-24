package com.fastaccess.provider.timeline.handler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.widget.TextView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.fastaccess.R;

import java.lang.ref.WeakReference;

/**
 * Created by Kosh on 22 Apr 2017, 7:44 PM
 */

public class DrawableGetter implements Html.ImageGetter, Drawable.Callback {
    private WeakReference<TextView> container;

    DrawableGetter(TextView tv) {
        tv.setTag(R.id.drawable_callback, this);
        this.container = new WeakReference<>(tv);
    }

    @Override public Drawable getDrawable(@NonNull String url) {
        final UrlDrawable urlDrawable = new UrlDrawable();
        if (container != null && container.get() != null) {
            Context context = container.get().getContext();
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_image);
            final GenericRequestBuilder load = Glide.with(context)
                    .load(url)
                    .dontAnimate()
                    .fallback(drawable)
                    .placeholder(drawable)
                    .error(drawable);
            final Target target = new GifBitmapTarget(urlDrawable);
            load.into(target);
        }
        return urlDrawable;
    }

    @Override public void invalidateDrawable(@NonNull Drawable drawable) {
        if (container != null && container.get() != null) {
            container.get().invalidate();
        }
    }

    @Override public void scheduleDrawable(@NonNull Drawable drawable, @NonNull Runnable runnable, long l) {}

    @Override public void unscheduleDrawable(@NonNull Drawable drawable, @NonNull Runnable runnable) {}

    private class GifBitmapTarget extends SimpleTarget<GlideDrawable> {
        private final UrlDrawable urlDrawable;

        GifBitmapTarget(UrlDrawable urlDrawable) {
            this.urlDrawable = urlDrawable;
        }

        @Override public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            if (container != null && container.get() != null) {
                TextView textView = container.get();
                float width;
                float height;
                if (resource.getIntrinsicWidth() >= textView.getWidth()) {
                    float downScale = (float) resource.getIntrinsicWidth() / textView.getWidth();
                    width = (float) resource.getIntrinsicWidth() / downScale;
                    height = (float) resource.getIntrinsicHeight() / downScale;
                } else {
                    float multiplier = (float) textView.getWidth() / resource.getIntrinsicWidth();
                    width = (float) resource.getIntrinsicWidth() * multiplier;
                    height = (float) resource.getIntrinsicHeight() * multiplier;
                }
                Rect rect = new Rect(0, 0, Math.round(width), Math.round(height));
                resource.setBounds(rect);
                urlDrawable.setBounds(rect);
                urlDrawable.setDrawable(resource);
                if (resource.isAnimated()) {
                    urlDrawable.setCallback((Drawable.Callback) textView.getTag(R.id.drawable_callback));
                    resource.setLoopCount(GlideDrawable.LOOP_FOREVER);
                    resource.start();
                }
                textView.setText(textView.getText());
                textView.invalidate();
            }
        }

    }

    class UrlDrawable extends BitmapDrawable implements Drawable.Callback {
        private Drawable drawable;

        @SuppressWarnings("deprecation") UrlDrawable() {}

        @Override public void draw(Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
                if (drawable instanceof GifDrawable) {
                    if (!((GifDrawable) drawable).isRunning()) {
                        ((GifDrawable) drawable).start();
                    }
                }
            }
        }

        public Drawable getDrawable() {
            return drawable;
        }

        public void setDrawable(Drawable drawable) {
            if (this.drawable != null) {
                this.drawable.setCallback(null);
            }
            drawable.setCallback(this);
            this.drawable = drawable;
        }

        @Override public void invalidateDrawable(@NonNull Drawable who) {
            if (getCallback() != null) {
                getCallback().invalidateDrawable(who);
            }
        }

        @Override public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
            if (getCallback() != null) {
                getCallback().scheduleDrawable(who, what, when);
            }
        }

        @Override public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
            if (getCallback() != null) {
                getCallback().unscheduleDrawable(who, what);
            }
        }
    }
}
