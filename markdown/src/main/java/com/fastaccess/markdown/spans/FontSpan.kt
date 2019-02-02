package com.fastaccess.markdown.spans

import android.text.ParcelableSpan
import android.text.TextPaint
import android.text.style.StyleSpan

class FontSpan(
        private val size: Float,
        style: Int
) : StyleSpan(style), ParcelableSpan {

    override fun updateMeasureState(p: TextPaint) {
        super.updateMeasureState(p)
        p.textSize = p.textSize * size
    }

    override fun updateDrawState(tp: TextPaint) {
        super.updateDrawState(tp)
        updateMeasureState(tp)
    }
}
