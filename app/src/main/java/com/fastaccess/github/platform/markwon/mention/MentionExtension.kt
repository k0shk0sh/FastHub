package com.fastaccess.github.platform.markwon.mention


import com.fastaccess.github.platform.markwon.mention.internal.MentionDelimiterProcessor
import com.fastaccess.github.platform.markwon.mention.internal.MentionNodeRenderer
import org.commonmark.Extension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlNodeRendererFactory
import org.commonmark.renderer.html.HtmlRenderer

/**
 * Created by kosh on 20/08/2017.
 */

class MentionExtension private constructor() : Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {

    override fun extend(parserBuilder: Parser.Builder) {
        parserBuilder.customDelimiterProcessor(MentionDelimiterProcessor())
    }

    override fun extend(rendererBuilder: HtmlRenderer.Builder) {
        rendererBuilder.nodeRendererFactory(HtmlNodeRendererFactory { MentionNodeRenderer(it) })
    }

    companion object {

        fun create(): Extension {
            return MentionExtension()
        }
    }
}
