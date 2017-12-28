package ru.noties.markwon.il;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

abstract class DrawableUtils {

    static void intrinsicBounds(@NonNull Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    private DrawableUtils() {}
}
