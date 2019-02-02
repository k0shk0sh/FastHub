package com.fastaccess.markdown.spans

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import net.nightwhistler.htmlspanner.SpanStack
import net.nightwhistler.htmlspanner.handlers.PreHandler
import org.htmlcleaner.ContentNode
import org.htmlcleaner.TagNode

/**
 * Created by Kosh on 22 Apr 2017, 1:07 PM
 */
class PreTagHandler(
        private val color: Int = 0,
        private val isPre: Boolean = false,
        private val isLightTheme: Boolean = true
) : PreHandler() {

    private fun getPlainText(buffer: StringBuffer, node: Any?) {
        if (node is ContentNode) {
            val text = node.content.toString()
            buffer.append(text)
        } else if (node is TagNode) {
            for (child in node.children) {
                this.getPlainText(buffer, child)
            }
        }
    }

    private fun replace(text: String): String {
        return text.replace("&nbsp;".toRegex(), "\u00A0")
                .replace("&amp;".toRegex(), "&")
                .replace("&quot;".toRegex(), "\"")
                .replace("&cent;".toRegex(), "¢")
                .replace("&lt;".toRegex(), "<")
                .replace("&gt;".toRegex(), ">")
                .replace("&sect;".toRegex(), "§")
                .replace("&ldquo;".toRegex(), "“")
                .replace("&rdquo;".toRegex(), "”")
                .replace("&lsquo;".toRegex(), "‘")
                .replace("&rsquo;".toRegex(), "’")
                .replace("&ndash;".toRegex(), "\u2013")
                .replace("&mdash;".toRegex(), "\u2014")
                .replace("&horbar;".toRegex(), "\u2015")
    }

    override fun handleTagNode(node: TagNode?, builder: SpannableStringBuilder?, start: Int, end: Int, spanStack: SpanStack?) {
        builder?.let {
            if (isPre) {
                val buffer = StringBuffer()
                buffer.append("\n")//fake padding top + make sure, pre is always by itself
                getPlainText(buffer, node)
                buffer.append("\n")//fake padding bottom + make sure, pre is always by itself
                builder.append(replace(buffer.toString()))
                builder.append("\n")
                builder.setSpan(CodeBackgroundRoundedSpan(color), start, builder.length, SPAN_EXCLUSIVE_EXCLUSIVE)
                builder.append("\n")
                this.appendNewLine(builder)
                this.appendNewLine(builder)
            } else {
                val text = node?.text ?: ""
                builder.append(" ")
                builder.append(replace(text.toString()))
                builder.append(" ")
                val stringStart = start + 1
                val stringEnd = builder.length - 1
                builder.setSpan(BackgroundColorSpan(color), stringStart, stringEnd, SPAN_EXCLUSIVE_EXCLUSIVE)
                if (isLightTheme) {
                    builder.setSpan(ForegroundColorSpan(Color.RED), stringStart, stringEnd, SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                builder.setSpan(TypefaceSpan("monospace"), stringStart, stringEnd, SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }
}
