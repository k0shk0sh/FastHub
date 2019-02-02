package com.fastaccess.markdown.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.style.LineHeightSpan
import android.text.style.ReplacementSpan

class HrSpan internal constructor(
        private val color: Int,
        private val width: Int
) : ReplacementSpan(), LineHeightSpan {

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        return paint.measureText(text, start, end).toInt()
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int,
                      y: Int, bottom: Int, paint: Paint) {
        val currentColor = paint.color
        paint.color = color
        paint.style = Paint.Style.FILL
        val height = 10
        canvas.drawRect(Rect(0, bottom - height, x.toInt() + width, bottom), paint)
        paint.color = currentColor
    }

    override fun chooseHeight(text: CharSequence, start: Int, end: Int, spanstartv: Int, v: Int, fm: Paint.FontMetricsInt) {
        fm.top /= 3
        fm.ascent /= 3
        fm.bottom /= 3
        fm.descent /= 3
    }
}