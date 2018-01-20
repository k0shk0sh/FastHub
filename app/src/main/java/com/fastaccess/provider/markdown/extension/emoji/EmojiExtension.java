package com.fastaccess.provider.markdown.extension.emoji;

import com.fastaccess.provider.markdown.extension.emoji.internal.EmojiDelimiterProcessor;
import com.fastaccess.provider.markdown.extension.emoji.internal.EmojiNodeRenderer;

import org.commonmark.Extension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * Created by kosh on 20/08/2017.
 */

public class EmojiExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {
    private EmojiExtension() {}

    public static Extension create() {
        return new EmojiExtension();
    }

    @Override public void extend(Parser.Builder parserBuilder) {
        parserBuilder.customDelimiterProcessor(new EmojiDelimiterProcessor());
    }

    @Override public void extend(HtmlRenderer.Builder rendererBuilder) {
        rendererBuilder.nodeRendererFactory(EmojiNodeRenderer::new);
    }
}
