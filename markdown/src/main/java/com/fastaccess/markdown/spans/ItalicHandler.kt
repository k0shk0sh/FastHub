package com.fastaccess.markdown.spans

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import net.nightwhistler.htmlspanner.SpanStack
import net.nightwhistler.htmlspanner.TagNodeHandler
import org.htmlcleaner.TagNode

/**
 * Created by Kosh on 06 May 2017, 11:02 AM
 */

class ItalicHandler : TagNodeHandler() {
    override fun handleTagNode(node: TagNode?, builder: SpannableStringBuilder?, start: Int, end: Int, spanStack: SpanStack?) {
        builder?.setSpan(FontSpan(1f, Typeface.ITALIC), start, builder.length, 33)
    }
}