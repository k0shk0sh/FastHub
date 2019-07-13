package com.fastaccess.markdown.spans

import android.text.SpannableStringBuilder
import android.text.Spanned
import net.nightwhistler.htmlspanner.SpanStack
import net.nightwhistler.htmlspanner.TagNodeHandler
import net.nightwhistler.htmlspanner.spans.CenterSpan
import org.htmlcleaner.TagNode

/**
 * Created by kosh on 30/07/2017.
 */

class HrHandler(
    private val color: Int = 0,
    private val width: Int = 0
) : TagNodeHandler() {

    override fun handleTagNode(node: TagNode?, builder: SpannableStringBuilder?, start: Int, end: Int, spanStack: SpanStack?) {
        builder?.let { spannableStringBuilder ->
            appendNewLine(spannableStringBuilder)
            val b = SpannableStringBuilder("$")
            val hrSpan = HrSpan(color, width)
            b.setSpan(hrSpan, 0, b.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            b.setSpan(CenterSpan(), 0, b.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            appendNewLine(b)
            spannableStringBuilder.append(b)
        }
    }
}
