package ru.noties.markwon.spans;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.noties.markwon.renderer.html.ImageSize;
import ru.noties.markwon.renderer.html.ImageSizeResolver;

public class AsyncDrawable extends Drawable {

    public interface Loader {

        void load(@NonNull String destination, @NonNull AsyncDrawable drawable);

        void cancel(@NonNull String destination);
    }

    private final String destination;
    private final Loader loader;
    private final ImageSize imageSize;
    private final ImageSizeResolver imageSizeResolver;

    private Drawable result;
    private Callback callback;

    private int canvasWidth;
    private float textSize;

    public AsyncDrawable(@NonNull String destination, @NonNull Loader loader) {
        this(destination, loader, null, null);
    }

    /**
     * @since 1.0.1
     */
    public AsyncDrawable(
            @NonNull String destination,
            @NonNull Loader loader,
            @Nullable ImageSizeResolver imageSizeResolver,
            @Nullable ImageSize imageSize
    ) {
        this.destination = destination;
        this.loader = loader;
        this.imageSizeResolver = imageSizeResolver;
        this.imageSize = imageSize;
    }

    public String getDestination() {
        return destination;
    }

    public Drawable getResult() {
        return result;
    }

    public boolean hasResult() {
        return result != null;
    }

    public boolean isAttached() {
        return getCallback() != null;
    }

    // yeah
    public void setCallback2(@Nullable Callback callback) {

        this.callback = callback;
        super.setCallback(callback);

        // if not null -> means we are attached
        if (callback != null) {
            loader.load(destination, this);
        } else {
            if (result != null) {

                result.setCallback(null);

                // let's additionally stop if it Animatable
                if (result instanceof Animatable) {
                    ((Animatable) result).stop();
                }
            }
            loader.cancel(destination);
        }
    }

    public void setResult(@NonNull Drawable result) {

        // if we have previous one, detach it
        if (this.result != null) {
            this.result.setCallback(null);
        }

        this.result = result;
        this.result.setCallback(callback);

        final Rect bounds = resolveBounds();
        result.setBounds(bounds);
        setBounds(bounds);

        invalidateSelf();
    }

    /**
     * @since 1.0.1
     */
    @SuppressWarnings("WeakerAccess")
    public void initWithKnownDimensions(int width, float textSize) {
        this.canvasWidth = width;
        this.textSize = textSize;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (hasResult()) {
            result.draw(canvas);
        }
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        final int opacity;
        if (hasResult()) {
            opacity = result.getOpacity();
        } else {
            opacity = PixelFormat.TRANSPARENT;
        }
        return opacity;
    }

    @Override
    public int getIntrinsicWidth() {
        final int out;
        if (hasResult()) {
            out = result.getIntrinsicWidth();
        } else {
            out = 0;
        }
        return out;
    }

    @Override
    public int getIntrinsicHeight() {
        final int out;
        if (hasResult()) {
            out = result.getIntrinsicHeight();
        } else {
            out = 0;
        }
        return out;
    }

    /**
     * @since 1.0.1
     */
    @NonNull
    private Rect resolveBounds() {
        final Rect rect;
        if (imageSizeResolver == null
                || imageSize == null) {
            rect = result.getBounds();
        } else {
            rect = imageSizeResolver.resolveImageSize(imageSize, result.getBounds(), canvasWidth, textSize);
        }
        return rect;
    }
}
