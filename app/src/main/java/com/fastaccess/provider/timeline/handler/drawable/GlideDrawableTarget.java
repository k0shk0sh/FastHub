package com.fastaccess.provider.timeline.handler.drawable;

import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fastaccess.R;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class GlideDrawableTarget extends SimpleTarget<Drawable> {
    private final UrlDrawable urlDrawable;
    private final WeakReference<TextView> container;
    private final int width;

    GlideDrawableTarget(UrlDrawable urlDrawable, WeakReference<TextView> container, int width) {
        this.urlDrawable = urlDrawable;
        this.container = container;
        this.width = width;
    }

    @Override public void onResourceReady(final @NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
        if (container != null && container.get() != null) {
            TextView textView = container.get();
            textView.post(() -> {
                float width;
                float height;
                if (resource.getIntrinsicWidth() >= this.width) {
                    float downScale = (float) resource.getIntrinsicWidth() / this.width;
                    width = (float) (resource.getIntrinsicWidth() / downScale / 1.3);
                    height = (float) (resource.getIntrinsicHeight() / downScale / 1.3);
                } else {
                    width =  (float) resource.getIntrinsicWidth();
                    height = (float) resource.getIntrinsicHeight();
                }
                Rect rect = new Rect(0, 0, Math.round(width), Math.round(height));
                resource.setBounds(rect);
                urlDrawable.setBounds(rect);
                urlDrawable.setDrawable(resource);
                if (resource instanceof GifDrawable) {
                    urlDrawable.setCallback((Drawable.Callback) textView.getTag(R.id.drawable_callback));
                    ((GifDrawable) resource).setLoopCount(GifDrawable.LOOP_FOREVER);
                    ((GifDrawable) resource).start();
                }
                textView.setText(textView.getText());
                textView.invalidate();
            });
        }
    }
}