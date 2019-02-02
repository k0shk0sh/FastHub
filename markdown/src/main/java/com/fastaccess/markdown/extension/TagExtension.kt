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

fun String.getLaughEmoji() = String(Character.toChars(0x1F601))
fun String.getSadEmoji() = String(Character.toChars(0x1F615))
fun String.getThumbsUpEmoji() = String(Character.toChars(0x1f44d))
fun String.getThumbsDownEmoji() = String(Character.toChars(0x1f44e))
fun String.getHoorayEmoji() = String(Character.toChars(0x1f389))
fun String.getHeartEmoji() = String(Character.toChars(0x2764))