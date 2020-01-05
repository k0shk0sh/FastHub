package com.fastaccess.markdown.spans

import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.LineBackgroundSpan
import android.text.style.MetricAffectingSpan
import android.text.style.TypefaceSpan
import androidx.annotation.ColorInt
import com.fastaccess.markdown.widget.SpannableBuilder

class DiffLineSpan private constructor(private val color: Int) : MetricAffectingSpan(), LineBackgroundSpan {
    private val rect = Rect()

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
        c: Canvas, p: Paint, left: Int, right: Int, top: Int, baseline: Int, bottom: Int, text: CharSequence, start: Int,
        end: Int, lnum: Int
    ) {
        val style = p.style
        val color = p.color
        p.style = Paint.Style.FILL
        p.color = this.color
        rect.set(left, top, right, bottom)
        c.drawRect(rect, p)
        p.color = color
        p.style = style
    }

    companion object {
        fun getSpannable(
            text: String?,
            @ColorInt patchAdditionColor: Int,
            @ColorInt patchDeletionColor: Int,
            @ColorInt patchRefColor: Int,
            newLine: Drawable? = null,
            truncate: Boolean = false
        ): SpannableStringBuilder {
            val builder = SpannableStringBuilder()
            if (!text.isNullOrEmpty()) {
                val split = text.split("\\r?\\n|\\r".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (split.isNotEmpty()) {
                    val lines = split.size
                    var index = -1
                    for (i in 0 until lines) {
                        if (truncate && lines - i > 2) continue
                        var token = split[i]
                        if (i < lines - 1) {
                            token += "\n"
                        }
                        val firstChar = token[0]
                        var color = Color.TRANSPARENT
                        when {
                            token.startsWith("@@") -> color = patchRefColor
                            firstChar == '+' -> color = patchAdditionColor
                            firstChar == '-' -> color = patchDeletionColor
                        }
                        index = token.indexOf("\\ No newline at end of file")
                        if (index != -1) {
                            token = token.replace("\\ No newline at end of file", "")
                        }
                        val spannableDiff = SpannableString(token)
                        if (color != Color.TRANSPARENT) {
                            val span = DiffLineSpan(color)
                            spannableDiff.setSpan(span, 0, token.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        builder.append(spannableDiff)
                    }
                    if (index != -1) {
                        builder.insert(builder.length - 1, SpannableBuilder.builder().append(newLine))
                    }
                }
            }
            builder.setSpan(TypefaceSpan("monospace"), 0, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            return builder
        }
    }

}