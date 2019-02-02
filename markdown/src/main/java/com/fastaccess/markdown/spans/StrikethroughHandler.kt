package com.fastaccess.markdown.spans

import android.text.SpannableStringBuilder
import android.text.style.StrikethroughSpan
import net.nightwhistler.htmlspanner.SpanStack
import net.nightwhistler.htmlspanner.TagNodeHandler
import org.htmlcleaner.TagNode

class StrikethroughHandler : TagNodeHandler() {

    override fun handleTagNode(node: TagNode?, builder: SpannableStringBuilder?, start: Int, end: Int, spanStack: SpanStack?) {
        builder?.setSpan(StrikethroughSpan(), start, end, 33)
    }
}