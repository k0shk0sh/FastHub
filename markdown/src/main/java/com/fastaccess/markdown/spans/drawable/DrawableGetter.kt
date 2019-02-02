package com.fastaccess.markdown.spans.drawable

import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.TextView
import androidx.annotation.NonNull
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fastaccess.github.extensions.getDrawableCompat
import com.fastaccess.markdown.R
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by Kosh on 22 Apr 2017, 7:44 PM
 */

class DrawableGetter(tv: TextView, private val width: Int) : Html.ImageGetter, Drawable.Callback {
    private val container: WeakReference<TextView>
    private val cachedTargets: MutableSet<GlideDrawableTarget>?

    init {
        tv.setTag(R.id.drawable_callback, this)
        this.container = WeakReference(tv)
        this.cachedTargets = HashSet()
    }

    override fun getDrawable(@NonNull url: String): Drawable {
        val urlDrawable = UrlDrawable()
        container.get()?.let {
            val context = it.context ?: return urlDrawable
            val target = GlideDrawableTarget(urlDrawable, container, width)
            Glide.with(context)
                    .applyDefaultRequestOptions(RequestOptions()
                            .override(width, width / 2)
                            .placeholder(context.getDrawableCompat(R.drawable.ic_image))
                            .dontAnimate())
                    .load(url)
                    .into(target)
            cachedTargets?.add(target)
        }
        return urlDrawable
    }

    override fun invalidateDrawable(@NonNull drawable: Drawable) {
        container.get()?.invalidate()
    }

    override fun scheduleDrawable(@NonNull drawable: Drawable, @NonNull runnable: Runnable, l: Long) {}

    override fun unscheduleDrawable(@NonNull drawable: Drawable, @NonNull runnable: Runnable) {}

    fun clear(@NonNull drawableGetter: DrawableGetter) {
        if (drawableGetter.cachedTargets != null) {
            for (target in drawableGetter.cachedTargets) {
                container.get()?.let { Glide.with(it.context).clear(target) }
            }
        }
    }
}
