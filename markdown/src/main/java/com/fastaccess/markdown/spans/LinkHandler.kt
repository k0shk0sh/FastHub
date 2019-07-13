package com.fastaccess.markdown.spans

import android.graphics.Color
import android.text.SpannableStringBuilder
import net.nightwhistler.htmlspanner.SpanStack
import net.nightwhistler.htmlspanner.TagNodeHandler
import org.htmlcleaner.TagNode

/**
 * Created by Kosh on 10 May 2017, 8:46 PM
 */

class LinkHandler : TagNodeHandler() {

    override fun handleTagNode(
        node: TagNode,
        builder: SpannableStringBuilder,
        start: Int,
        end: Int,
        spanStack: SpanStack
    ) {
        val href = node.getAttributeByName("href")
        val url = if (!href.isNullOrEmpty()) {
            href.toString()
        } else if (!node.text.isNullOrEmpty()) {
            "https://github.com/${node.text}"
        } else {
            null
        }

        url?.let { builder.setSpan(UrlSpan(href, linkColor), start, end, 33) }
    }

    companion object {
        private val linkColor = Color.parseColor("#4078C0")
    }
}
