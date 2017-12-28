package ru.noties.markwon;

import android.support.annotation.NonNull;

public interface UrlProcessor {
    @NonNull
    String process(@NonNull String destination);
}
