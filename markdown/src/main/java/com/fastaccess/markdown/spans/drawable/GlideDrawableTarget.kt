package com.fastaccess.markdown.spans.drawable

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.widget.TextView
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.fastaccess.markdown.R
import timber.log.Timber
import java.lang.ref.WeakReference

internal class GlideDrawableTarget(
        private val urlDrawable: UrlDrawable,
        private val container: WeakReference<TextView>?,
        private val width: Int
) : SimpleTarget<Drawable>() {

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        if (container?.get() != null) {
            val textView = container.get()
            textView?.post {
                val width: Float
                val height: Float
                if (resource.intrinsicWidth >= this.width) {
                    val downScale = resource.intrinsicWidth.toFloat() / this.width
                    width = (resource.intrinsicWidth.toDouble() / downScale.toDouble() / 1.3).toFloat()
                    height = (resource.intrinsicHeight.toDouble() / downScale.toDouble() / 1.3).toFloat()
                } else {
                    val multiplier = this.width.toFloat() / resource.intrinsicWidth
                    width = resource.intrinsicWidth.toFloat() * multiplier
                    height = resource.intrinsicHeight.toFloat() * multiplier
                }
                val rect = Rect(0, 0, Math.round(width), Math.round(height))
                resource.bounds = rect
                urlDrawable.bounds = rect
                urlDrawable.setDrawable(resource)
                Timber.e("${resource is GifDrawable}")
                if (resource is GifDrawable) {
                    urlDrawable.callback = textView.getTag(R.id.drawable_callback) as Drawable.Callback
                    resource.setLoopCount(GifDrawable.LOOP_FOREVER)
                    resource.start()
                }
                textView.text = textView.text
                textView.invalidate()
            }
        }
    }
}