package ru.noties.markwon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

class SyntaxHighlightNoOp implements SyntaxHighlight {
    @NonNull
    @Override
    public CharSequence highlight(@Nullable String info, @NonNull String code) {
        return code;
    }
}
