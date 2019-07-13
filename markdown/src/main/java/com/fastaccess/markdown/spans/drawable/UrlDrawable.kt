package com.fastaccess.markdown.spans.drawable

import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

import com.bumptech.glide.load.resource.gif.GifDrawable

class UrlDrawable : BitmapDrawable(), Drawable.Callback {

    private var drawable: Drawable? = null

    override fun draw(canvas: Canvas) {
        drawable?.let {
            it.draw(canvas)
            if (it is GifDrawable) {
                if (!it.isRunning) it.start()
            }
        }
    }

    override fun invalidateDrawable(who: Drawable) {
        if (callback != null) {
            callback!!.invalidateDrawable(who)
        }
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        if (callback != null) {
            callback!!.scheduleDrawable(who, what, `when`)
        }
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        if (callback != null) {
            callback!!.unscheduleDrawable(who, what)
        }
    }

    fun setDrawable(drawable: Drawable) {
        this.drawable?.callback = this
        drawable.callback = this
        this.drawable = drawable
    }
}