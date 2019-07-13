package com.fastaccess.markdown.widget

import android.graphics.Typeface.BOLD
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.*
import android.view.View
import androidx.annotation.ColorInt

class SpannableBuilder private constructor() : SpannableStringBuilder() {

    fun append(text: CharSequence, span: Any?): SpannableBuilder {
        if (!text.isEmpty()) {
            append(text = text)
            if (span != null) {
                val length = length
                setSpan(span, length - text.length, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return this
    }

    override fun append(text: Char): SpannableBuilder {
        if (text.toInt() != 0) super.append(text)
        return this
    }

    override fun append(text: CharSequence?): SpannableBuilder {
        if (text != null && text.isNotEmpty()) super.append(text)
        return this
    }

    fun append(drawable: Drawable?): SpannableBuilder {
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            append(' ', CenterImageSpan(drawable))
        }
        return this
    }

    fun append(text: Char, span: Any): SpannableBuilder {
        append(text)
        val length = length
        setSpan(span, length - 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return this
    }

    fun bold(text: CharSequence, span: Any): SpannableBuilder {
        var text = text
        if (!text.isEmpty()) {
            text = builder().bold(text)
            append(text, span)
        }
        return this
    }

    fun bold(text: CharSequence? = null): SpannableBuilder {
        return if (text != null && text.isNotEmpty()) {
            append(text, StyleSpan(BOLD))
        } else {
            this
        }
    }

    fun background(text: CharSequence, color: Int): SpannableBuilder {
        return if (!text.isEmpty()) append(text, BackgroundColorSpan(color)) else this
    }

    fun foreground(text: CharSequence, @ColorInt color: Int): SpannableBuilder {
        return if (!text.isEmpty()) append(text, ForegroundColorSpan(color)) else this
    }

    fun foreground(text: Char, @ColorInt color: Int): SpannableBuilder {
        return append(text, ForegroundColorSpan(color))
    }

    fun url(text: CharSequence, listener: View.OnClickListener): SpannableBuilder {
        return if (!text.isEmpty()) append(text, object : URLSpan(text.toString()) {
            override fun onClick(widget: View) {
                listener.onClick(widget)
            }
        }) else this
    }

    fun url(text: CharSequence): SpannableBuilder {
        return if (!text.isEmpty()) append(text, URLSpan(text.toString())) else this
    }

    fun clickable(text: CharSequence, listener: View.OnClickListener? = null): SpannableBuilder {
        return if (!text.isEmpty()) append(text, object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.color = ds.linkColor
                ds.isUnderlineText = false
            }

            override fun onClick(widget: View) {
                listener?.onClick(widget)
            }
        }) else this
    }

    fun space(): SpannableBuilder {
        this.append(text = " ")
        return this
    }

    fun newline(): SpannableBuilder {
        this.append("\n")
        return this
    }

    companion object {

        fun builder(): SpannableBuilder {
            return SpannableBuilder()
        }
    }

}

