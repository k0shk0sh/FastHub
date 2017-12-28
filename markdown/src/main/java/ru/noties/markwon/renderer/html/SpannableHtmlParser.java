package ru.noties.markwon.renderer.html;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;

import java.util.HashMap;
import java.util.Map;

import ru.noties.markwon.LinkResolverDef;
import ru.noties.markwon.UrlProcessor;
import ru.noties.markwon.UrlProcessorNoOp;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.LinkSpan;
import ru.noties.markwon.spans.SpannableTheme;

@SuppressWarnings("WeakerAccess")
public class SpannableHtmlParser {

    // creates default parser
    @NonNull
    public static SpannableHtmlParser create(
            @NonNull SpannableTheme theme,
            @NonNull AsyncDrawable.Loader loader
    ) {
        return builderWithDefaults(theme, loader, null, null, null)
                .build();
    }

    /**
     * @since 1.0.1
     */
    @NonNull
    public static SpannableHtmlParser create(
            @NonNull SpannableTheme theme,
            @NonNull AsyncDrawable.Loader loader,
            @NonNull ImageSizeResolver imageSizeResolver
    ) {
        return builderWithDefaults(theme, loader, null, null, imageSizeResolver)
                .build();
    }

    @NonNull
    public static SpannableHtmlParser create(
            @NonNull SpannableTheme theme,
            @NonNull AsyncDrawable.Loader loader,
            @NonNull UrlProcessor urlProcessor,
            @NonNull LinkSpan.Resolver resolver
    ) {
        return builderWithDefaults(theme, loader, urlProcessor, resolver, null)
                .build();
    }

    /**
     * @since 1.0.1
     */
    @NonNull
    public static SpannableHtmlParser create(
            @NonNull SpannableTheme theme,
            @NonNull AsyncDrawable.Loader loader,
            @NonNull UrlProcessor urlProcessor,
            @NonNull LinkSpan.Resolver resolver,
            @NonNull ImageSizeResolver imageSizeResolver
    ) {
        return builderWithDefaults(theme, loader, urlProcessor, resolver, imageSizeResolver)
                .build();
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    @NonNull
    public static Builder builderWithDefaults(@NonNull SpannableTheme theme) {
        return builderWithDefaults(theme, null, null, null, null);
    }

    /**
     * Updated in 1.0.1: added imageSizeResolverArgument
     */
    @NonNull
    public static Builder builderWithDefaults(
            @NonNull SpannableTheme theme,
            @Nullable AsyncDrawable.Loader asyncDrawableLoader,
            @Nullable UrlProcessor urlProcessor,
            @Nullable LinkSpan.Resolver resolver,
            @Nullable ImageSizeResolver imageSizeResolver
    ) {

        if (urlProcessor == null) {
            urlProcessor = new UrlProcessorNoOp();
        }

        if (resolver == null) {
            resolver = new LinkResolverDef();
        }

        final BoldProvider boldProvider = new BoldProvider();
        final ItalicsProvider italicsProvider = new ItalicsProvider();
        final StrikeProvider strikeProvider = new StrikeProvider();

        final ImageProvider imageProvider;
        if (asyncDrawableLoader != null) {

            if (imageSizeResolver == null) {
                imageSizeResolver = new ImageSizeResolverDef();
            }

            imageProvider = new ImageProviderImpl(theme, asyncDrawableLoader, urlProcessor, imageSizeResolver);
        } else {
            imageProvider = null;
        }

        return new Builder()
                .simpleTag("b", boldProvider)
                .simpleTag("strong", boldProvider)
                .simpleTag("i", italicsProvider)
                .simpleTag("em", italicsProvider)
                .simpleTag("cite", italicsProvider)
                .simpleTag("dfn", italicsProvider)
                .simpleTag("sup", new SuperScriptProvider(theme))
                .simpleTag("sub", new SubScriptProvider(theme))
                .simpleTag("u", new UnderlineProvider())
                .simpleTag("del", strikeProvider)
                .simpleTag("s", strikeProvider)
                .simpleTag("strike", strikeProvider)
                .simpleTag("a", new LinkProvider(theme, urlProcessor, resolver))
                .imageProvider(imageProvider);
    }

    // for simple tags without arguments
    // <b>, <i>, etc
    public interface SpanProvider {
        Object provide(@NonNull Tag tag);
    }

    public interface ImageProvider {
        Spanned provide(@NonNull Tag tag);
    }

    public interface HtmlParser {

        // returns span for a simple content
        Object getSpan(@NonNull String html);

        Spanned parse(@NonNull String html);
    }

    private final Map<String, SpanProvider> simpleTags;
    private final ImageProvider imageProvider;
    private final HtmlParser parser;
    private final TagParser tagParser;

    private SpannableHtmlParser(Builder builder) {
        this.simpleTags = builder.simpleTags;
        this.imageProvider = builder.imageProvider;
        this.parser = builder.parser;
        this.tagParser = new TagParser();
    }

    @Nullable
    public Tag parseTag(String html) {
        return tagParser.parse(html);
    }

    @Nullable
    public Object getSpanForTag(@NonNull Tag tag) {

        // check if we have specific handler for tag.name

        final Object out;

        final SpanProvider provider = simpleTags.get(tag.name);
        if (provider != null) {
            out = provider.provide(tag);
        } else {
            // let's prepare mock content & extract spans from it
            // actual content doesn't matter, here it's just `abc`
            final String mock = tag.raw + "abc" + "</" + tag.name + ">";
            out = parser.getSpan(mock);
        }

        return out;
    }

    // if tag is NULL, then it's HtmlBlock... else just a void tag
    public Spanned getSpanned(@Nullable Tag tag, String html) {
        final Spanned spanned;
        if (tag != null && "img".equals(tag.name) && imageProvider != null) {
            spanned = imageProvider.provide(tag);
        } else {
            spanned = parser.parse(html);
        }
        return spanned;
    }

    public static class Builder {

        private final Map<String, SpanProvider> simpleTags = new HashMap<>(3);

        private ImageProvider imageProvider;
        private HtmlParser parser;

        @NonNull
        Builder simpleTag(@NonNull String tag, @NonNull SpanProvider provider) {
            simpleTags.put(tag, provider);
            return this;
        }

        @NonNull
        public Builder imageProvider(@Nullable ImageProvider imageProvider) {
            this.imageProvider = imageProvider;
            return this;
        }

        @NonNull
        public Builder parser(@NonNull HtmlParser parser) {
            this.parser = parser;
            return this;
        }

        @NonNull
        public SpannableHtmlParser build() {
            if (parser == null) {
                parser = DefaultHtmlParser.create();
            }
            return new SpannableHtmlParser(this);
        }
    }

    public static class Tag {

        private final String raw;
        private final String name;
        private final Map<String, String> attributes;

        private final boolean opening;
        private final boolean voidTag;

        public Tag(String raw, String name, @NonNull Map<String, String> attributes, boolean opening, boolean voidTag) {
            this.raw = raw;
            this.name = name;
            this.attributes = attributes;
            this.opening = opening;
            this.voidTag = voidTag;
        }

        public String raw() {
            return raw;
        }

        public String name() {
            return name;
        }

        @NonNull
        public Map<String, String> attributes() {
            return attributes;
        }

        public boolean opening() {
            return opening;
        }

        public boolean voidTag() {
            return voidTag;
        }

        @Override
        public String toString() {
            return "Tag{" +
                    "raw='" + raw + '\'' +
                    ", name='" + name + '\'' +
                    ", attributes=" + attributes +
                    ", opening=" + opening +
                    ", voidTag=" + voidTag +
                    '}';
        }
    }

    public static abstract class DefaultHtmlParser implements HtmlParser {

        public static DefaultHtmlParser create() {
            final DefaultHtmlParser parser;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                parser = new Parser24();
            } else {
                parser = new ParserPre24();
            }
            return parser;
        }

        Object getSpan(Spanned spanned) {

            final Object out;

            final Object[] spans;
            final int length = spanned != null ? spanned.length() : 0;
            if (length == 0) {
                spans = null;
            } else {
                spans = spanned.getSpans(0, length, Object.class);
            }

            if (spans != null
                    && spans.length > 0) {
                out = spans[0];
            } else {
                out = null;
            }

            return out;
        }

        @SuppressWarnings("deprecation")
        private static class ParserPre24 extends DefaultHtmlParser {

            @Override
            public Object getSpan(@NonNull String html) {
                return getSpan(parse(html));
            }

            @Override
            public Spanned parse(@NonNull String html) {
                return Html.fromHtml(html, null, null);
            }
        }

        @TargetApi(Build.VERSION_CODES.N)
        private static class Parser24 extends DefaultHtmlParser {

            @Override
            public Object getSpan(@NonNull String html) {
                return getSpan(parse(html));
            }

            @Override
            public Spanned parse(@NonNull String html) {
                return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT, null, null);
            }
        }
    }
}
