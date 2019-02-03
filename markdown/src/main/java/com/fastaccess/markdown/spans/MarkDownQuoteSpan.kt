package com.fastaccess.markdown.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.Layout
import android.text.style.LeadingMarginSpan
import com.fastaccess.github.extensions.toPx


/**
 * Created by zhou on 16-6-25.
 * 引用Span
 */
class MarkDownQuoteSpan(private val color: Int) : LeadingMarginSpan {

    private val paint = Paint()
    private val rect = Rect()


    override fun drawLeadingMargin(
        c: Canvas,
        p: Paint,
        x: Int,
        dir: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        first: Boolean,
        layout: Layout) {
        val width = 4.toPx()
        paint.set(p)
        paint.style = Paint.Style.FILL
        paint.color = color
        val left: Int
        val right: Int
        val l = x + dir * width
        val r = l + dir * width
        left = Math.min(l, r)
        right = Math.max(l, r)
        rect.set(left, top, right, bottom)
        c.drawRect(rect, paint)
    }

    override fun getLeadingMargin(first: Boolean) = 24.toPx()

}
