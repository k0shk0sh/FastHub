package ru.noties.markwon;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

@SuppressWarnings({"unused", "WeakerAccess"})
public class UrlProcessorAndroidAssets implements UrlProcessor {

    private final UrlProcessorRelativeToAbsolute assetsProcessor
            = new UrlProcessorRelativeToAbsolute("file:///android_asset/");

    private final UrlProcessor processor;

    public UrlProcessorAndroidAssets() {
        this(null);
    }

    public UrlProcessorAndroidAssets(@Nullable UrlProcessor parent) {
        this.processor = parent;
    }

    @NonNull
    @Override
    public String process(@NonNull String destination) {
        final String out;
        final Uri uri = Uri.parse(destination);
        if (TextUtils.isEmpty(uri.getScheme())) {
            out = assetsProcessor.process(destination);
        } else {
            if (processor != null) {
                out = processor.process(destination);
            } else {
                out = destination;
            }
        }
        return out;
    }
}
