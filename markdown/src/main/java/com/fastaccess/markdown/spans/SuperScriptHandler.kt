package com.fastaccess.markdown.spans

import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import net.nightwhistler.htmlspanner.SpanStack
import net.nightwhistler.htmlspanner.TagNodeHandler
import org.htmlcleaner.TagNode

class SuperScriptHandler : TagNodeHandler() {

    override fun handleTagNode(node: TagNode?, builder: SpannableStringBuilder?, start: Int, end: Int, spanStack: SpanStack?) {
        builder?.let {
            builder.setSpan(SuperscriptSpan(), start, end, 33)
            builder.setSpan(RelativeSizeSpan(0.8f), start, end, 33)
        }
    }
}