package com.fastaccess.markdown.spans

import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import com.fastaccess.github.extensions.isTrue
import net.nightwhistler.htmlspanner.SpanStack
import net.nightwhistler.htmlspanner.TextUtil
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
            val text = TextUtil.replaceHtmlEntities(node.content.toString(), true)
            buffer.append(text)
        } else if (node is TagNode) {
            for (child in node.allChildren) {
                getPlainText(buffer, child)
            }
        }
    }

    private fun replace(text: String): String {
        return text.replace("&nbsp;".toRegex(), "\u00A0")
            .replace("&amp;", "&", true)
            .replace("&quot;", "\"", true)
            .replace("&cent;", "¢", true)
            .replace("&lt;", "<", true)
            .replace("&gt;", ">", true)
            .replace("&sect;", "§", true)
            .replace("&ldquo;", "“", true)
            .replace("&rdquo;", "”", true)
            .replace("&lsquo;", "‘", true)
            .replace("&rsquo;", "’", true)
            .replace("&ndash;", "\u2013", true)
            .replace("&mdash;", "\u2014", true)
            .replace("&horbar;", "\u2015", true)
    }

    override fun beforeChildren(node: TagNode?, builder: SpannableStringBuilder?, spanStack: SpanStack?) {
        super.beforeChildren(node, builder, spanStack)
        isPre.isTrue { node?.addChild(ContentNode("\n")) } // append fake hr
    }

    override fun handleTagNode(node: TagNode?, builder: SpannableStringBuilder?, start: Int, end: Int, spanStack: SpanStack?) {
        builder?.let {
            if (isPre) {
                val buffer = StringBuffer()
                buffer.append("\n")//fake padding top + make sure, pre is always by itself
                getPlainText(buffer, node)
                builder.append(replace(buffer.toString()))
                this.appendNewLine(builder)
                builder.setSpan(CodeBackgroundRoundedSpan(color), start, builder.length, SPAN_EXCLUSIVE_EXCLUSIVE)
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
