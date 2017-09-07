package com.fastaccess.provider.timeline.handler.drawable;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

class UrlDrawable extends BitmapDrawable {
    private Drawable drawable;

    @SuppressWarnings("deprecation") UrlDrawable() {}

    @Override public void draw(Canvas canvas) {
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}