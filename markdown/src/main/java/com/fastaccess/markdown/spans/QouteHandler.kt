package com.fastaccess.markdown.spans

import android.text.SpannableStringBuilder
import net.nightwhistler.htmlspanner.SpanStack

import net.nightwhistler.htmlspanner.TagNodeHandler

import org.htmlcleaner.TagNode

/**
 * Created by Kosh on 23 Apr 2017, 11:30 AM
 */

class QouteHandler(private val color: Int = 0) : TagNodeHandler() {
    override fun handleTagNode(node: TagNode?, builder: SpannableStringBuilder?, start: Int, end: Int, spanStack: SpanStack?) {
        builder?.let {
            builder.append("\n")
            builder.setSpan(MarkDownQuoteSpan(color), if (start > builder.length - 1) start + 1 else start, builder.length - 1, 33)
            builder.append("\n")
        }
    }
}
