package com.fastaccess.markdown.spans

import android.text.TextPaint
import android.text.style.URLSpan

/**
 * Created by zhou on 16-7-2.
 * 链接Span
 */
class LinkSpan(
        url: String,
        private val color: Int
) : URLSpan(url) {

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.color = color
        ds.isUnderlineText = false
    }

}
