package ru.noties.markwon.renderer.html;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @since 1.0.1
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ImageSizeResolverDef extends ImageSizeResolver {

    // we track these two, others are considered to be pixels
    protected static final String UNIT_PERCENT = "%";
    protected static final String UNIT_EM = "em";

    @NonNull
    @Override
    public Rect resolveImageSize(
            @Nullable ImageSize imageSize,
            @NonNull Rect imageBounds,
            int canvasWidth,
            float textSize
    ) {

        if (imageSize == null) {
            return imageBounds;
        }

        final Rect rect;

        final ImageSize.Dimension width = imageSize.width;
        final ImageSize.Dimension height = imageSize.height;

        final int imageWidth = imageBounds.width();
        final int imageHeight = imageBounds.height();

        final float ratio = (float) imageWidth / imageHeight;

        if (width != null) {

            final int w;
            final int h;

            if (UNIT_PERCENT.equals(width.unit)) {
                w = (int) (canvasWidth * (width.value / 100.F) + .5F);
            } else {
                w = resolveAbsolute(width, imageWidth, textSize);
            }

            if (height == null
                    || UNIT_PERCENT.equals(height.unit)) {
                h = (int) (w / ratio + .5F);
            } else {
                h = resolveAbsolute(height, imageHeight, textSize);
            }

            rect = new Rect(0, 0, w, h);

        } else if (height != null) {

            if (!UNIT_PERCENT.equals(height.unit)) {
                final int h = resolveAbsolute(height, imageHeight, textSize);
                final int w = (int) (h * ratio + .5F);
                rect = new Rect(0, 0, w, h);
            } else {
                rect = imageBounds;
            }
        } else {
            rect = imageBounds;
        }

        return rect;
    }

    protected int resolveAbsolute(@NonNull ImageSize.Dimension dimension, int original, float textSize) {
        final int out;
        if (UNIT_EM.equals(dimension.unit)) {
            out = (int) (dimension.value * textSize + .5F);
        } else {
            out = original;
        }
        return out;
    }
}
