package com.fastaccess.provider.markdown.extension.mention;

import com.fastaccess.provider.markdown.extension.mention.internal.MentionDelimiterProcessor;
import com.fastaccess.provider.markdown.extension.mention.internal.MentionNodeRenderer;

import org.commonmark.Extension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * Created by kosh on 20/08/2017.
 */

public class MentionExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {
    private MentionExtension() {}

    public static Extension create() {
        return new MentionExtension();
    }

    @Override public void extend(Parser.Builder parserBuilder) {
        parserBuilder.customDelimiterProcessor(new MentionDelimiterProcessor());
    }

    @Override public void extend(HtmlRenderer.Builder rendererBuilder) {
        rendererBuilder.nodeRendererFactory(MentionNodeRenderer::new);
    }
}
