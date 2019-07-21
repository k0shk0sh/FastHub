package com.fastaccess.markdown.extension.markwon.emoji

import android.text.SpannedString
import com.fastaccess.markdown.emoji.EmojiManager
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonVisitor
import org.commonmark.parser.Parser
import timber.log.Timber


class EmojiPlugin : AbstractMarkwonPlugin() {

    override fun configureParser(builder: Parser.Builder) {
        builder.extensions(setOf(EmojiExtension.create()))
    }

    override fun configureVisitor(builder: MarkwonVisitor.Builder) {
        builder.on(Emoji::class.java) { visitor, emoji ->
            val length = visitor.length()
            val emojiUnicode = emoji.emoji
            val unicode = EmojiManager.getForAlias(emoji.emoji)?.unicode
            if (!unicode.isNullOrEmpty()) {
                visitor.setSpans(length, SpannedString(unicode))
            } else {
                Timber.e(emojiUnicode)
            }
        }
    }

    companion object {
        fun create() = EmojiPlugin()
    }
}
