package com.fastaccess.markdown.spans.drawable

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.graphics.drawable.DrawableWrapper
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import timber.log.Timber

@SuppressLint("RestrictedApi")
class GlideDrawableTarget(private val width: Int) : SimpleTarget<Drawable>() {


    private val transparentDrawable = ColorDrawable(Color.TRANSPARENT)
    private val wrapper = DrawableWrapper(null)

    val lazyDrawable get() = wrapper

    init {
        setDrawable(null)
    }

    override fun onLoadStarted(placeholder: Drawable?) {
        setDrawable(placeholder)
    }


    override fun onLoadFailed(errorDrawable: Drawable?) {
        setDrawable(errorDrawable)
    }


    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        Timber.e("${resource is Animatable}---${resource is GifDrawable}")
        if (resource is GifDrawable) {
            resource.setLoopCount(GifDrawable.LOOP_FOREVER)
            resource.start()
        }
        setDrawable(resource)
    }


    override fun onLoadCleared(placeholder: Drawable?) {
        setDrawable(placeholder)
    }

    private fun setDrawable(resource: Drawable?) {
        var drawable = resource
        if (drawable == null) {
            drawable = transparentDrawable
        }
        val width: Float
        val height: Float
        if (drawable.intrinsicWidth >= this.width) {
            val downScale = drawable.intrinsicWidth.toFloat() / this.width
            width = (drawable.intrinsicWidth.toDouble() / downScale.toDouble() / 1.3).toFloat()
            height = (drawable.intrinsicHeight.toDouble() / downScale.toDouble() / 1.3).toFloat()
        } else {
            val multiplier = this.width.toFloat() / drawable.intrinsicWidth
            width = drawable.intrinsicWidth.toFloat() * multiplier
            height = drawable.intrinsicHeight.toFloat() * multiplier
        }
        val rect = Rect(0, 0, width.toInt(), height.toInt())
        wrapper.wrappedDrawable = drawable
        wrapper.bounds = rect
        drawable.bounds = wrapper.bounds
        wrapper.invalidateSelf()
    }
}