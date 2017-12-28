package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.net.MalformedURLException;
import java.net.URL;

@SuppressWarnings("WeakerAccess")
public class UrlProcessorRelativeToAbsolute implements UrlProcessor {

    private final URL base;

    public UrlProcessorRelativeToAbsolute(@NonNull String base) {
        this.base = obtain(base);
    }

    @NonNull
    @Override
    public String process(@NonNull String destination) {

        String out = destination;

        if (base != null) {
            try {
                final URL u = new URL(base, destination);
                out = u.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return out;
    }

    @Nullable
    private static URL obtain(String base) {
        try {
            return new URL(base);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
