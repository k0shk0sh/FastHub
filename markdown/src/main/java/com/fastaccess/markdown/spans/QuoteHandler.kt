package com.fastaccess.markdown.spans

import android.text.SpannableStringBuilder
import net.nightwhistler.htmlspanner.SpanStack

import net.nightwhistler.htmlspanner.TagNodeHandler

import org.htmlcleaner.TagNode

/**
 * Created by Kosh on 23 Apr 2017, 11:30 AM
 */

class QuoteHandler(private val color: Int = 0) : TagNodeHandler() {
    override fun handleTagNode(
        node: TagNode?,
        builder: SpannableStringBuilder?,
        start: Int,
        end: Int,
        spanStack: SpanStack?
    ) {
        builder?.setSpan(MdQouteSpan(color), if (start > builder.length - 1) start + 1 else start, builder.length - 1, 33)
    }
}
