package com.fastaccess.github.platform.markwon.emoji.internal


import com.fastaccess.github.platform.markwon.emoji.Emoji
import org.commonmark.node.Text
import org.commonmark.parser.delimiter.DelimiterProcessor
import org.commonmark.parser.delimiter.DelimiterRun

class EmojiDelimiterProcessor : DelimiterProcessor {

    override fun getOpeningCharacter(): Char = ':'

    override fun getClosingCharacter(): Char = ':'

    override fun getMinLength(): Int = 1

    override fun getDelimiterUse(
        opener: DelimiterRun,
        closer: DelimiterRun
    ): Int = if (opener.length() >= 1 && closer.length() >= 1) {
        1
    } else {
        0
    }

    override fun process(
        opener: Text,
        closer: Text,
        delimiterCount: Int
    ) {
        var emoji: Emoji? = null
        val text = opener.next
        if (text is Text) {
            emoji = Emoji()
            emoji.emoji = text.literal
            text.unlink()
        }
        if (emoji != null) opener.insertAfter(emoji)
    }
}
