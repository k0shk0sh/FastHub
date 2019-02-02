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

    override fun handleTagNode(node: TagNode, builder: SpannableStringBuilder, start: Int, end: Int, spanStack: SpanStack) {
        val href = node.getAttributeByName("href")
        if (href != null) {
            builder.setSpan(LinkSpan(href, linkColor), start, end, 33)
        } else if (node.text != null) {
            builder.setSpan(LinkSpan("https://github.com/" + node.text.toString(), linkColor), start, end, 33)
        }
    }

    companion object {
        private val linkColor = Color.parseColor("#4078C0")
    }
}
