package ru.noties.markwon.renderer.html;

import android.support.annotation.Nullable;

/**
 * @since 1.0.1
 */
@SuppressWarnings("WeakerAccess")
public class ImageSize {

    public static class Dimension {

        public final float value;
        public final String unit;

        public Dimension(float value, @Nullable String unit) {
            this.value = value;
            this.unit = unit;
        }

        @Override
        public String toString() {
            return "Dimension{" +
                    "value=" + value +
                    ", unit='" + unit + '\'' +
                    '}';
        }
    }

    public final Dimension width;
    public final Dimension height;

    public ImageSize(@Nullable Dimension width, @Nullable Dimension height) {
        this.width = width;
        this.height = height;
    }
}
