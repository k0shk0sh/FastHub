package com.fastaccess.markdown.spans

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.RectF
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.CharacterStyle
import android.text.style.ReplacementSpan
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import com.fastaccess.github.extensions.generateTextColor
import java.util.*

/**
 * adopted class from Android source code & modified to fit FastHub need.
 */

class LabelSpan private constructor(private val color: Int, private val dims: SpanDimensions) : ReplacementSpan() {

    private val txtPaint = TextPaint()
    private val fontMetrics = FontMetricsInt()

    interface SpanDimensions {
        val padding: Int

        val paddingExtraWidth: Int

        val paddingAfter: Int

        val maxWidth: Int

        val roundedCornerRadius: Float

        val marginTop: Int

        val isRtl: Boolean
    }

    init {
        txtPaint.bgColor = color
    }

    constructor(color: Int, roundedCoroner: Float = 5f) : this(color, object : SpanDimensions {
        override val padding: Int
            get() = 6

        override val paddingExtraWidth: Int
            get() = 0

        override val paddingAfter: Int
            get() = 0

        override//random number
        val maxWidth: Int
            get() = 1000

        override val roundedCornerRadius: Float
            get() = roundedCoroner

        override val marginTop: Int
            get() = 8

        override val isRtl: Boolean
            get() = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL
    })

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: FontMetricsInt?): Int {
        setupFontMetrics(text, start, end, fm, paint)
        if (fm != null) {
            val padding = dims.padding
            val margin = dims.marginTop
            fm.ascent = Math.min(fm.top, fm.ascent - padding) - margin
            fm.descent = Math.max(fm.bottom, padding)
            fm.top = fm.ascent
            fm.bottom = fm.descent
        }
        return measureWidth(txtPaint, text, start, end, dims.isRtl)
    }

    private fun measureWidth(paint: Paint, text: CharSequence, start: Int, end: Int,
                             includePaddingAfter: Boolean): Int {
        val paddingW = dims.padding + dims.paddingExtraWidth
        val maxWidth = dims.maxWidth
        var w = paint.measureText(text, start, end).toInt() + 2 * paddingW
        if (includePaddingAfter) {
            w += dims.paddingAfter
        }
        if (w > maxWidth) {
            w = maxWidth
        }
        return w
    }

    private fun setupFontMetrics(text: CharSequence, start: Int, end: Int, fm: FontMetricsInt?, p: Paint) {
        txtPaint.set(p)
        val otherSpans = (text as Spanned).getSpans(start, end, CharacterStyle::class.java)
        for (otherSpan in otherSpans) {
            otherSpan.updateDrawState(txtPaint)
        }
        txtPaint.textSize = p.textSize
        if (fm != null) {
            txtPaint.getFontMetricsInt(fm)
        }
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int,
                      x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        var textVal = text
        var startVal = start
        var endVal = end
        var yVal = y
        val padding = dims.padding
        val paddingW = padding + dims.paddingExtraWidth
        val maxWidth = dims.maxWidth
        setupFontMetrics(textVal, startVal, endVal, fontMetrics, paint)
        val bgWidth = measureWidth(txtPaint, textVal, startVal, endVal, false)
        fontMetrics.top = Math.min(fontMetrics.top, fontMetrics.ascent - padding)
        fontMetrics.bottom = Math.max(fontMetrics.bottom, padding)
        val topVal = yVal + fontMetrics.top - fontMetrics.bottom
        val bottomVal = yVal
        yVal = bottomVal - fontMetrics.bottom
        val isRtl = dims.isRtl
        val paddingAfter = dims.paddingAfter
        if (txtPaint.bgColor != 0) {
            val prevColor = txtPaint.color
            val prevStyle = txtPaint.style
            txtPaint.color = txtPaint.bgColor
            txtPaint.style = Paint.Style.FILL
            val left: Float = if (isRtl) {
                x + paddingAfter
            } else {
                x
            }
            val right = left + bgWidth
            val rect = RectF(left, topVal.toFloat(), right, bottomVal.toFloat())
            val radius = dims.roundedCornerRadius
            canvas.drawRoundRect(rect, radius, radius, txtPaint)
            txtPaint.color = prevColor
            txtPaint.style = prevStyle
        }
        if (bgWidth == maxWidth) {
            textVal = TextUtils.ellipsize(textVal.subSequence(startVal, endVal).toString(), txtPaint, (bgWidth - 2 * paddingW).toFloat(), TextUtils.TruncateAt.MIDDLE)
            startVal = 0
            endVal = textVal.length
        }
        var textX = x + paddingW
        if (isRtl) {
            textX += paddingAfter.toFloat()
        }
        if (color != Color.TRANSPARENT) txtPaint.color = txtPaint.color.generateTextColor()
        canvas.drawText(textVal, startVal, endVal, textX, yVal.toFloat(), txtPaint)
    }
}