package com.fastaccess.markdown.extension

import android.text.SpannableStringBuilder
import net.nightwhistler.htmlspanner.TagNodeHandler
import net.nightwhistler.htmlspanner.spans.FontFamilySpan

/**
 * Created by Kosh on 02.02.19.
 */

fun TagNodeHandler.getFontFamilySpan(builder: SpannableStringBuilder, start: Int, end: Int): FontFamilySpan? {
    val spans = builder.getSpans(start, end, FontFamilySpan::class.java) as Array<FontFamilySpan>
    return if (spans.isNotEmpty()) spans[spans.size - 1] else null
}