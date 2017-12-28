package ru.noties.markwon;

import android.support.annotation.NonNull;

import ru.noties.markwon.spans.AsyncDrawable;

class AsyncDrawableLoaderNoOp implements AsyncDrawable.Loader {
    @Override
    public void load(@NonNull String destination, @NonNull AsyncDrawable drawable) {

    }

    @Override
    public void cancel(@NonNull String destination) {

    }
}
