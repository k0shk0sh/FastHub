package com.fastaccess.markdown.spans.drawable

import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.TextView
import androidx.annotation.NonNull
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.fastaccess.github.extensions.getDrawableCompat
import com.fastaccess.markdown.R
import timber.log.Timber
import java.lang.ref.WeakReference

/**
 * Created by Kosh on 22 Apr 2017, 7:44 PM
 */

class DrawableGetter(
    tv: TextView,
    private val width: Int,
    private val url: String
) : Html.ImageGetter, Drawable.Callback {
    private val container: WeakReference<TextView>
    private val cachedTargets = hashSetOf<GlideDrawableTarget<out Drawable>>()

    init {
        tv.setTag(R.id.drawable_callback, this)
        this.container = WeakReference(tv)
    }

    override fun getDrawable(oriUrl: String): Drawable {
        val urlDrawable = UrlDrawable()
        container.get()?.let {
            Timber.e("${it.tag}")
            val context = it.context ?: return urlDrawable
            val imageTarget = if (oriUrl.endsWith(".gif")) {
                GlideDrawableTarget<GifDrawable>(urlDrawable, container, width)
            } else {
                GlideDrawableTarget<Drawable>(urlDrawable, container, width)
            }
            Glide.with(it).apply {
                applyDefaultRequestOptions(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(context.getDrawableCompat(R.drawable.ic_image)))
                if (oriUrl.endsWith(".gif")) {
                    asGif().load(url).into(imageTarget as GlideDrawableTarget<GifDrawable>)
                } else {
                    asDrawable().load(url).into(imageTarget as GlideDrawableTarget<Drawable>)
                }
            }
            cachedTargets.add(imageTarget)
        }
        return urlDrawable
    }

    override fun invalidateDrawable(@NonNull drawable: Drawable) {
        container.get()?.invalidate()
    }

    override fun scheduleDrawable(@NonNull drawable: Drawable, @NonNull runnable: Runnable, l: Long) {}

    override fun unscheduleDrawable(@NonNull drawable: Drawable, @NonNull runnable: Runnable) {}

    fun clear(@NonNull drawableGetter: DrawableGetter) {
        Timber.e("clearing......")
        for (target in drawableGetter.cachedTargets) {
            container.get()?.let { Glide.with(it).clear(target) }
        }
    }
}
