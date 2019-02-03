package com.fastaccess.markdown.spans.drawable

import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.fastaccess.github.extensions.getDrawableCompat
import com.fastaccess.markdown.R
import java.util.*

/**
 * Created by Kosh on 22 Apr 2017, 7:44 PM
 */

class DrawableGetter(
    private val targetView: TextView,
    private val width: Int
) : Html.ImageGetter, Drawable.Callback {
    private val imageTargets = ArrayList<Target<out Drawable>>()

    init {
        targetView.tag = this
    }

    override fun getDrawable(url: String): Drawable {
        val imageTarget = if (url.endsWith(".gif")) {
            GlideDrawableTarget<GifDrawable>(width)
        } else {
            GlideDrawableTarget<Drawable>(width)
        }
        val asyncWrapper = imageTarget.lazyDrawable
        asyncWrapper.callback = this
        Glide.with(targetView).apply {
            applyDefaultRequestOptions(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(targetView.context?.getDrawableCompat(R.drawable.ic_image)))
            if (url.endsWith(".gif")) {
                asGif().load(url).into(imageTarget as GlideDrawableTarget<GifDrawable>)
            } else {
                asDrawable().load(url).into(imageTarget as GlideDrawableTarget<Drawable>)
            }
        }
        imageTargets.add(imageTarget)
        return asyncWrapper
    }

    fun clear() {
        for (target in imageTargets) {
            Glide.with(targetView).clear(target)
        }
    }

    fun clear(view: TextView) {
        view.text = null
        val tag = view.tag
        if (tag is DrawableGetter) {
            tag.clear()
            view.tag = null
        }
    }

    override fun invalidateDrawable(who: Drawable) {
        targetView.invalidate()
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {}

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {}
}

