package com.fastaccess.provider.timeline.handler.drawable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.widget.TextView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.fastaccess.R;
import com.fastaccess.helper.Logger;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kosh on 22 Apr 2017, 7:44 PM
 */

public class DrawableGetter implements Html.ImageGetter, Drawable.Callback {
    private WeakReference<TextView> container;
    private final Set<GifBitmapTarget> cachedTargets;

    public DrawableGetter(TextView tv) {
        tv.setTag(R.id.drawable_callback, this);
        this.container = new WeakReference<>(tv);
        this.cachedTargets = new HashSet<>();
    }

    public void clear(@NonNull DrawableGetter drawableGetter) {
        if (drawableGetter.cachedTargets != null) {
            for (GifBitmapTarget target : drawableGetter.cachedTargets) {
                Logger.e("is clearing");
                Glide.clear(target);
            }
        }
    }

    @Override public Drawable getDrawable(@NonNull String url) {
        final UrlDrawable urlDrawable = new UrlDrawable();
        if (container != null && container.get() != null) {
            Context context = container.get().getContext();
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_image);
            urlDrawable.setDrawable(drawable);
            container.get().setText(container.get().getText());
            container.get().invalidate();
            final GenericRequestBuilder load = Glide.with(context)
                    .load(url)
                    .dontAnimate();
            final GifBitmapTarget target = new GifBitmapTarget(urlDrawable, container);
            load.into(target);
            cachedTargets.add(target);
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
}
