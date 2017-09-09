package com.fastaccess.provider.timeline.handler.drawable;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.resource.gif.GifDrawable;

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