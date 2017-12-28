package ru.noties.markwon.renderer.html;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ru.noties.markwon.UrlProcessor;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.AsyncDrawableSpan;
import ru.noties.markwon.spans.SpannableTheme;

class ImageProviderImpl implements SpannableHtmlParser.ImageProvider {

    private final SpannableTheme theme;
    private final AsyncDrawable.Loader loader;
    private final UrlProcessor urlProcessor;
    private final ImageSizeResolver imageSizeResolver;

    ImageProviderImpl(
            @NonNull SpannableTheme theme,
            @NonNull AsyncDrawable.Loader loader,
            @NonNull UrlProcessor urlProcessor,
            @NonNull ImageSizeResolver imageSizeResolver
    ) {
        this.theme = theme;
        this.loader = loader;
        this.urlProcessor = urlProcessor;
        this.imageSizeResolver = imageSizeResolver;
    }

    @Override
    public Spanned provide(@NonNull SpannableHtmlParser.Tag tag) {

        final Spanned spanned;

        final Map<String, String> attributes = tag.attributes();
        final String src = attributes.get("src");
        final String alt = attributes.get("alt");

        if (!TextUtils.isEmpty(src)) {

            final String destination = urlProcessor.process(src);

            final String replacement;
            if (!TextUtils.isEmpty(alt)) {
                replacement = alt;
            } else {
                replacement = "\uFFFC";
            }

            final AsyncDrawable drawable = new AsyncDrawable(destination, loader, imageSizeResolver, parseImageSize(attributes));
            final AsyncDrawableSpan span = new AsyncDrawableSpan(theme, drawable);

            final SpannableString string = new SpannableString(replacement);
            string.setSpan(span, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            spanned = string;
        } else {
            spanned = null;
        }

        return spanned;
    }

    @Nullable
    private static ImageSize parseImageSize(@NonNull Map<String, String> attributes) {

        final ImageSize imageSize;

        final StyleProvider styleProvider = new StyleProvider(attributes.get("style"));

        final ImageSize.Dimension width = parseDimension(extractDimension("width", attributes, styleProvider));
        final ImageSize.Dimension height = parseDimension(extractDimension("height", attributes, styleProvider));

        if (width == null
                && height == null) {
            imageSize = null;
        } else {
            imageSize = new ImageSize(width, height);
        }

        return imageSize;
    }

    @Nullable
    private static String extractDimension(@NonNull String name, @NonNull Map<String, String> attributes, @NonNull StyleProvider styleProvider) {

        final String out;

        final String inline = attributes.get(name);
        if (!TextUtils.isEmpty(inline)) {
            out = inline;
        } else {
            out = extractDimensionFromStyle(name, styleProvider);
        }

        return out;
    }

    @Nullable
    private static String extractDimensionFromStyle(@NonNull String name, @NonNull StyleProvider styleProvider) {
        return styleProvider.attributes().get(name);
    }

    @Nullable
    private static ImageSize.Dimension parseDimension(@Nullable String raw) {

        // a set of digits, then dimension unit (allow floating)

        final ImageSize.Dimension dimension;

        final int length = raw != null
                ? raw.length()
                : 0;

        if (length == 0) {
            dimension = null;
        } else {

            // first digit to find -> unit is finished (can be null)

            int index = -1;

            for (int i = length - 1; i >= 0; i--) {
                if (Character.isDigit(raw.charAt(i))) {
                    index = i;
                    break;
                }
            }

            // no digits -> no dimension
            if (index == -1) {
                dimension = null;
            } else {

                final String value;
                final String unit;

                // no unit is specified
                if (index == length - 1) {
                    value = raw;
                    unit = null;
                } else {
                    value = raw.substring(0, index + 1);
                    unit = raw.substring(index + 1);
                }

                ImageSize.Dimension inner;
                try {
                    final float floatValue = Float.parseFloat(value);
                    inner = new ImageSize.Dimension(floatValue, unit);
                } catch (NumberFormatException e) {
                    inner = null;
                }

                dimension = inner;
            }
        }

        return dimension;
    }

    private static class StyleProvider {

        private final String style;
        private Map<String, String> attributes;

        StyleProvider(@Nullable String style) {
            this.style = style;
        }

        @NonNull
        Map<String, String> attributes() {
            final Map<String, String> out;
            if (attributes != null) {
                out = attributes;
            } else {
                if (TextUtils.isEmpty(style)) {
                    out = attributes = Collections.emptyMap();
                } else {
                    final String[] split = style.split(";");
                    final Map<String, String> map = new HashMap<>(split.length);
                    String[] parts;
                    for (String s : split) {
                        if (!TextUtils.isEmpty(s)) {
                            parts = s.split(":");
                            if (parts.length == 2) {
                                map.put(parts[0].trim(), parts[1].trim());
                            }
                        }
                    }
                    out = attributes = map;
                }
            }
            return out;
        }
    }
}
