package com.fastaccess.provider.timeline.handler

import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import net.nightwhistler.htmlspanner.TagNodeHandler
import net.nightwhistler.htmlspanner.spans.FontFamilySpan
import org.htmlcleaner.TagNode

/**
 * Created by Kosh on 29.09.17.
 */
class HeaderHandler(val size: Float) : TagNodeHandler() {

    override fun beforeChildren(node: TagNode?, builder: SpannableStringBuilder?) {
        appendNewLine(builder)
    }

    override fun handleTagNode(node: TagNode, builder: SpannableStringBuilder, start: Int, end: Int) {
        builder.setSpan(RelativeSizeSpan(this.size), start, end, 33)
        val originalSpan = this.getFontFamilySpan(builder, start, end)
        val boldSpan: FontFamilySpan
        if (originalSpan == null) {
            boldSpan = FontFamilySpan(this.spanner.defaultFont)
        } else {
            boldSpan = FontFamilySpan(originalSpan.fontFamily)
            boldSpan.isItalic = originalSpan.isItalic
        }

        boldSpan.isBold = true
        builder.setSpan(boldSpan, start, end, 33)
        appendNewLine(builder)
    }
}