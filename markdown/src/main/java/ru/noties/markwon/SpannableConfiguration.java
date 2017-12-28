package ru.noties.markwon;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.noties.markwon.renderer.html.ImageSizeResolver;
import ru.noties.markwon.renderer.html.ImageSizeResolverDef;
import ru.noties.markwon.renderer.html.SpannableHtmlParser;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.LinkSpan;
import ru.noties.markwon.spans.SpannableTheme;

@SuppressWarnings("WeakerAccess")
public class SpannableConfiguration {

    // creates default configuration
    @NonNull
    public static SpannableConfiguration create(@NonNull Context context) {
        return new Builder(context).build();
    }

    @NonNull
    public static Builder builder(@NonNull Context context) {
        return new Builder(context);
    }

    private final SpannableTheme theme;
    private final AsyncDrawable.Loader asyncDrawableLoader;
    private final SyntaxHighlight syntaxHighlight;
    private final LinkSpan.Resolver linkResolver;
    private final UrlProcessor urlProcessor;
    private final SpannableHtmlParser htmlParser;

    private SpannableConfiguration(@NonNull Builder builder) {
        this.theme = builder.theme;
        this.asyncDrawableLoader = builder.asyncDrawableLoader;
        this.syntaxHighlight = builder.syntaxHighlight;
        this.linkResolver = builder.linkResolver;
        this.urlProcessor = builder.urlProcessor;
        this.htmlParser = builder.htmlParser;
    }

    @NonNull
    public SpannableTheme theme() {
        return theme;
    }

    @NonNull
    public AsyncDrawable.Loader asyncDrawableLoader() {
        return asyncDrawableLoader;
    }

    @NonNull
    public SyntaxHighlight syntaxHighlight() {
        return syntaxHighlight;
    }

    @NonNull
    public LinkSpan.Resolver linkResolver() {
        return linkResolver;
    }

    @NonNull
    public UrlProcessor urlProcessor() {
        return urlProcessor;
    }

    @NonNull
    public SpannableHtmlParser htmlParser() {
        return htmlParser;
    }

    @SuppressWarnings("unused")
    public static class Builder {

        private final Context context;
        private SpannableTheme theme;
        private AsyncDrawable.Loader asyncDrawableLoader;
        private SyntaxHighlight syntaxHighlight;
        private LinkSpan.Resolver linkResolver;
        private UrlProcessor urlProcessor;
        private SpannableHtmlParser htmlParser;
        private ImageSizeResolver imageSizeResolver;

        Builder(@NonNull Context context) {
            this.context = context;
        }

        @NonNull
        public Builder theme(@NonNull SpannableTheme theme) {
            this.theme = theme;
            return this;
        }

        @NonNull
        public Builder asyncDrawableLoader(@NonNull AsyncDrawable.Loader asyncDrawableLoader) {
            this.asyncDrawableLoader = asyncDrawableLoader;
            return this;
        }

        @NonNull
        public Builder syntaxHighlight(@NonNull SyntaxHighlight syntaxHighlight) {
            this.syntaxHighlight = syntaxHighlight;
            return this;
        }

        @NonNull
        public Builder linkResolver(@NonNull LinkSpan.Resolver linkResolver) {
            this.linkResolver = linkResolver;
            return this;
        }

        @NonNull
        public Builder urlProcessor(@NonNull UrlProcessor urlProcessor) {
            this.urlProcessor = urlProcessor;
            return this;
        }

        @NonNull
        public Builder htmlParser(@NonNull SpannableHtmlParser htmlParser) {
            this.htmlParser = htmlParser;
            return this;
        }

        /**
         * @since 1.0.1
         */
        @NonNull
        public Builder imageSizeResolver(@NonNull ImageSizeResolver imageSizeResolver) {
            this.imageSizeResolver = imageSizeResolver;
            return this;
        }

        @NonNull
        public SpannableConfiguration build() {

            if (theme == null) {
                theme = SpannableTheme.create(context);
            }

            if (asyncDrawableLoader == null) {
                asyncDrawableLoader = new AsyncDrawableLoaderNoOp();
            }

            if (syntaxHighlight == null) {
                syntaxHighlight = new SyntaxHighlightNoOp();
            }

            if (linkResolver == null) {
                linkResolver = new LinkResolverDef();
            }

            if (urlProcessor == null) {
                urlProcessor = new UrlProcessorNoOp();
            }

            if (htmlParser == null) {

                if (imageSizeResolver == null) {
                    imageSizeResolver = new ImageSizeResolverDef();
                }

                htmlParser = SpannableHtmlParser.create(theme, asyncDrawableLoader, urlProcessor, linkResolver, imageSizeResolver);
            }

            return new SpannableConfiguration(this);
        }
    }

}
