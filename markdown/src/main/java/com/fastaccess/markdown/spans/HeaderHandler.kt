package com.fastaccess.markdown.spans

import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import com.fastaccess.markdown.extension.getFontFamilySpan
import net.nightwhistler.htmlspanner.SpanStack
import net.nightwhistler.htmlspanner.TagNodeHandler
import net.nightwhistler.htmlspanner.spans.FontFamilySpan
import org.htmlcleaner.TagNode

/**
 * Created by Kosh on 29.09.17.
 */
class HeaderHandler(private val size: Float) : TagNodeHandler() {


    override fun beforeChildren(node: TagNode?, builder: SpannableStringBuilder?, spanStack: SpanStack?) {
        builder?.let { appendNewLine(it) }
    }

    override fun handleTagNode(node: TagNode?, builder: SpannableStringBuilder?, start: Int, end: Int, spanStack: SpanStack?) {
        builder?.let { b ->
            b.setSpan(RelativeSizeSpan(this.size), start, end, 33)
            val originalSpan = getFontFamilySpan(b, start, end)
            val boldSpan: FontFamilySpan
            if (originalSpan == null) {
                boldSpan = FontFamilySpan(spanner.fontResolver.defaultFont)
            } else {
                boldSpan = FontFamilySpan(originalSpan.fontFamily)
                boldSpan.isItalic = originalSpan.isItalic
            }

            boldSpan.isBold = true
            b.setSpan(boldSpan, start, end, 33)
            appendNewLine(b)
        }
    }
}