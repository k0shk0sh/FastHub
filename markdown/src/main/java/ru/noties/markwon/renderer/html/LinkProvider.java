package ru.noties.markwon.renderer.html;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Map;

import ru.noties.markwon.UrlProcessor;
import ru.noties.markwon.spans.LinkSpan;
import ru.noties.markwon.spans.SpannableTheme;

class LinkProvider implements SpannableHtmlParser.SpanProvider {

    private final SpannableTheme theme;
    private final UrlProcessor urlProcessor;
    private final LinkSpan.Resolver resolver;

    LinkProvider(
            @NonNull SpannableTheme theme,
            @NonNull UrlProcessor urlProcessor,
            @NonNull LinkSpan.Resolver resolver) {
        this.theme = theme;
        this.urlProcessor = urlProcessor;
        this.resolver = resolver;
    }

    @Override
    public Object provide(@NonNull SpannableHtmlParser.Tag tag) {

        final Object span;

        final Map<String, String> attributes = tag.attributes();
        final String href = attributes.get("href");
        if (!TextUtils.isEmpty(href)) {

            final String destination = urlProcessor.process(href);
            span = new LinkSpan(theme, destination, resolver);

        } else {
            span = null;
        }

        return span;
    }
}
