package ru.noties.markwon.renderer.html;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @since 1.0.1
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class ImageSizeResolver {

    /**
     * We do not expose canvas height deliberately. As we cannot rely on this value very much
     *
     * @param imageSize   {@link ImageSize} parsed from HTML
     * @param imageBounds original image bounds
     * @param canvasWidth width of the canvas
     * @param textSize    current font size
     * @return resolved image bounds
     */
    @NonNull
    public abstract Rect resolveImageSize(
            @Nullable ImageSize imageSize,
            @NonNull Rect imageBounds,
            int canvasWidth,
            float textSize
    );
}
