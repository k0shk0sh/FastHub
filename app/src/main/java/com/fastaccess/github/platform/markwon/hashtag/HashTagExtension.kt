package com.fastaccess.github.platform.markwon.hashtag


import com.fastaccess.github.platform.markwon.hashtag.internal.HashTagDelimiterProcessor
import com.fastaccess.github.platform.markwon.hashtag.internal.HashTagNodeRenderer
import org.commonmark.Extension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

/**
 * Created by kosh on 20/08/2017.
 */

class HashTagExtension private constructor() : Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {

    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customDelimiterProcessor(HashTagDelimiterProcessor())
    }

    override fun extend(rendererBuilder: HtmlRenderer.Builder) {
        rendererBuilder.nodeRendererFactory { HashTagNodeRenderer(it) }
    }

    companion object {
        fun create(): Extension {
            return HashTagExtension()
        }
    }
}
