package com.fastaccess.github.platform.markwon.emoji

import com.fastaccess.github.platform.markwon.emoji.internal.EmojiDelimiterProcessor
import com.fastaccess.github.platform.markwon.emoji.internal.EmojiNodeRenderer
import org.commonmark.Extension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

/**
 * Created by kosh on 20/08/2017.
 */

class EmojiExtension private constructor() : Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {

    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customDelimiterProcessor(EmojiDelimiterProcessor())
    }

    override fun extend(rendererBuilder: HtmlRenderer.Builder) {
        rendererBuilder.nodeRendererFactory { EmojiNodeRenderer(it) }
    }

    companion object {

        fun create(): Extension {
            return EmojiExtension()
        }
    }
}
