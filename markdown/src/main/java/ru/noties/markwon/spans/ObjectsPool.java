package ru.noties.markwon.spans;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

abstract class ObjectsPool {

    // maybe it's premature optimization, but as all the drawing is done in one thread
    // and we apply needed values before actual drawing it's (I assume) safe to reuse some frequently used objects

    // if one of the spans need some really specific handling for Paint object (like colorFilters, masks, etc)
    // it should instantiate own instance of it

    private static final Rect RECT = new Rect();
    private static final RectF RECT_F = new RectF();
    private static final Paint PAINT = new Paint(Paint.ANTI_ALIAS_FLAG);

    static Rect rect() {
        return RECT;
    }

    static RectF rectF() {
        return RECT_F;
    }

    static Paint paint() {
        return PAINT;
    }

    private ObjectsPool() {
    }
}
