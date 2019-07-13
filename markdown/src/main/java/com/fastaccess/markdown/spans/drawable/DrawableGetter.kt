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

/**
 * Created by Kosh on 22 Apr 2017, 7:44 PM
 */

class DrawableGetter(
    private val tv: TextView?,
    private val width: Int,
    private val url: String
) : Html.ImageGetter, Drawable.Callback {
    private val cachedTargets = hashSetOf<GlideDrawableTarget<out Drawable>>()

    init {
        tv?.setTag(R.id.drawable_callback, this)
    }

    override fun getDrawable(oriUrl: String): Drawable {
        val urlDrawable = UrlDrawable()
        tv?.let {
            val context = it.context ?: return urlDrawable
            val imageTarget = if (oriUrl.endsWith(".gif")) {
                GlideDrawableTarget<GifDrawable>(urlDrawable, tv, width)
            } else {
                GlideDrawableTarget<Drawable>(urlDrawable, tv, width)
            }
            Glide.with(it).apply {
                clear(it)
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
        tv?.invalidate()
    }

    override fun scheduleDrawable(@NonNull drawable: Drawable, @NonNull runnable: Runnable, l: Long) {}

    override fun unscheduleDrawable(@NonNull drawable: Drawable, @NonNull runnable: Runnable) {}

    fun clear(@NonNull drawableGetter: DrawableGetter) {
        Timber.e("clearing......")
        for (target in drawableGetter.cachedTargets) {
            tv?.let { Glide.with(it).clear(target) }
        }
    }
}
