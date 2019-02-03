package com.fastaccess.markdown.spans

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.widget.TextView
import com.fastaccess.markdown.spans.drawable.DrawableGetter
import net.nightwhistler.htmlspanner.SpanStack
import net.nightwhistler.htmlspanner.TagNodeHandler
import org.htmlcleaner.ContentNode
import org.htmlcleaner.TagNode
import timber.log.Timber

/**
 * Created by Kosh on 22 Apr 2017, 1:09 PM
 */

class DrawableHandler(
    private val textView: TextView,
    private val width: Int = 0
) : TagNodeHandler() {
    override fun beforeChildren(node: TagNode?, builder: SpannableStringBuilder?, spanStack: SpanStack?) {
        super.beforeChildren(node, builder, spanStack)
        node?.addChild(ContentNode("\n"))
    }

    override fun handleTagNode(node: TagNode?, builder: SpannableStringBuilder?, start: Int, end: Int, spanStack: SpanStack?) {
        node?.let { n ->
            builder?.let { b ->
                val src = n.getAttributeByName("fallback-src") ?: n.getAttributeByName("data-canonical-src") ?: n.getAttributeByName("src")
                Timber.e("$src")
                if (!src.isNullOrEmpty()) {
                    b.append("￼")
                    this.appendNewLine(b)
                    val imageGetter = DrawableGetter(textView, width)
                    b.setSpan(ImageSpan(imageGetter.getDrawable(src)), start, b.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//                    b.setSpan(CenterSpan(), start, b.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // no center for now
                    this.appendNewLine(b)
                }
            }
        }
    }
}
