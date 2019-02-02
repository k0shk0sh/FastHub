package com.fastaccess.markdown.spans

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.widget.TextView
import com.fastaccess.markdown.spans.drawable.DrawableGetter
import net.nightwhistler.htmlspanner.SpanStack
import net.nightwhistler.htmlspanner.TagNodeHandler
import net.nightwhistler.htmlspanner.spans.CenterSpan
import org.htmlcleaner.TagNode

/**
 * Created by Kosh on 22 Apr 2017, 1:09 PM
 */

class DrawableHandler(
        private val textView: TextView,
        private val width: Int = 0
) : TagNodeHandler() {
    override fun handleTagNode(node: TagNode?, builder: SpannableStringBuilder?, start: Int, end: Int, spanStack: SpanStack?) {
        node?.let { n ->
            builder?.let { b ->
                val src = n.getAttributeByName("src")
                if (!src.isNullOrEmpty()) {
                    b.append("￼")
                    b.append("\n")
                    val imageGetter = DrawableGetter(textView, width)
                    b.setSpan(ImageSpan(imageGetter.getDrawable(src)), start, b.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    b.setSpan(CenterSpan(), start, b.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    b.append("\n")
                }
            }
        }
    }
}
