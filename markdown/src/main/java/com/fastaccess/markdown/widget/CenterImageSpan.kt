package com.fastaccess.markdown.widget

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.DynamicDrawableSpan

class CenterImageSpan(private val icon: Drawable) : DynamicDrawableSpan() {
    override fun getDrawable(): Drawable = icon
    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val rect = drawable.bounds
        if (fm != null) {
            fm.ascent = -rect.bottom
            fm.descent = 0

            fm.top = fm.ascent
            fm.bottom = 0
        }

        return rect.right
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val d = drawable
        canvas.save()
        var transY = bottom - d.bounds.bottom / 2
        val fm = paint.fontMetricsInt
        transY -= fm.descent - fm.ascent / 2

        canvas.translate(x, transY.toFloat())
        d.draw(canvas)
        canvas.restore()
    }
}