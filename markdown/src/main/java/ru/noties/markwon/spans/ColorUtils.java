package ru.noties.markwon.spans;

abstract class ColorUtils {

    static int applyAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    private ColorUtils() {
    }
}
