package com.fastaccess.markdown.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.Layout
import android.text.style.LeadingMarginSpan
import androidx.annotation.ColorInt
import com.fastaccess.github.extensions.toPx
import kotlin.math.max
import kotlin.math.min

/**
 * Created by Kosh on 2019-06-30.
 */
class MdQouteSpan(
    @ColorInt private val paintColor: Int
) : LeadingMarginSpan {

    private val rect = Rect()
    private val paint = Paint()

    override fun getLeadingMargin(first: Boolean): Int = LEADING_MARGIN.toPx()
    override fun drawLeadingMargin(
        c: Canvas?,
        p: Paint?,
        x: Int,
        dir: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence?,
        start: Int,
        end: Int,
        first: Boolean,
        layout: Layout?
    ) {
        val w = WIDTH.toPx()
        paint.set(p)
        paint.style = Paint.Style.FILL
        paint.color = paintColor
        val leftWithMargin = x + dir * w
        val rightWithMargin = leftWithMargin + dir * w
        val left = min(leftWithMargin, rightWithMargin)
        val right = max(leftWithMargin, rightWithMargin)
        rect.set(left, top, right, bottom)
        c?.drawRect(rect, paint)

    }

    companion object {
        private const val LEADING_MARGIN = 24
        private const val WIDTH = 4
    }
}