package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public interface SyntaxHighlight {

    @NonNull
    CharSequence highlight(@Nullable String info, @NonNull String code);
}
