package ru.noties.markwon.extension.emoji;

import org.commonmark.Extension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import ru.noties.markwon.extension.emoji.internal.EmojiDelimiterProcessor;
import ru.noties.markwon.extension.emoji.internal.EmojiNodeRenderer;

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
