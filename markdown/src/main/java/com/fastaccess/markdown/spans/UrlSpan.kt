package com.fastaccess.markdown.spans

import android.text.TextPaint
import android.text.style.URLSpan

/**
 * Created by Kosh on 2019-06-30.
 */
class UrlSpan (
    url: String,
    private val textColor: Int
) : URLSpan(url) {
    override fun updateDrawState(textPaint: TextPaint) {
        super.updateDrawState(textPaint)
        textPaint.color = textColor
        textPaint.isUnderlineText = false
    }
}