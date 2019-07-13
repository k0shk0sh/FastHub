package com.fastaccess.markdown.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Layout
import android.text.TextPaint
import android.text.style.LeadingMarginSpan
import android.text.style.LineBackgroundSpan
import android.text.style.MetricAffectingSpan

class CodeBackgroundRoundedSpan constructor(
        private val color: Int
) : MetricAffectingSpan(), LeadingMarginSpan, LineBackgroundSpan {

    private val rect = RectF()

    override fun updateMeasureState(paint: TextPaint) {
        apply(paint)
    }

    override fun updateDrawState(paint: TextPaint) {
        apply(paint)
    }

    private fun apply(paint: TextPaint) {
        paint.typeface = Typeface.MONOSPACE
    }

    override fun drawBackground(
            c: Canvas, p: Paint, left: Int, right: Int, top: Int, baseline: Int, bottom: Int,
            text: CharSequence, start: Int, end: Int, lnum: Int
    ) {
        val style = p.style
        val color = p.color
        p.style = Paint.Style.FILL
        p.color = this.color
        rect.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
        c.drawRect(rect, p)
        p.color = color
        p.style = style
    }

    override fun getLeadingMargin(first: Boolean) = 30

    override fun drawLeadingMargin(
            c: Canvas, p: Paint, x: Int, dir: Int, top: Int, baseline: Int, bottom: Int,
            text: CharSequence, start: Int, end: Int, first: Boolean, layout: Layout
    ) = Unit
}