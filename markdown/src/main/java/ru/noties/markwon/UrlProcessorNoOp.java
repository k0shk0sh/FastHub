package ru.noties.markwon;

import android.support.annotation.NonNull;

public class UrlProcessorNoOp implements UrlProcessor {
    @NonNull
    @Override
    public String process(@NonNull String destination) {
        return destination;
    }
}
