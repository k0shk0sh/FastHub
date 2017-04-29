package com.fastaccess.ui.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ViewOutlineProvider;
import android.widget.RelativeLayout;

import com.fastaccess.R;

/**
 * An extension to {@link RelativeLayout} which has a foreground drawable.
 */
public class ForegroundRelativeLayout extends RelativeLayout {

    private Drawable foreground;

    @SuppressLint("CustomViewStyleable")
    public ForegroundRelativeLayout(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ForegroundView);
        final Drawable d = a.getDrawable(R.styleable.ForegroundView_android_foreground);
        if (d != null) {
            setForeground(d);
        }
        a.recycle();
        setOutlineProvider(ViewOutlineProvider.BOUNDS);
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (foreground != null) {
            foreground.setBounds(0, 0, w, h);
        }
    }

    @Override public boolean hasOverlappingRendering() {
        return false;
    }

    @Override protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || (who == foreground);
    }

    @Override public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (foreground != null) foreground.jumpToCurrentState();
    }

    @Override protected void drawableStateChanged() {
        if (isInEditMode()) return;
        super.drawableStateChanged();
        if (foreground != null && foreground.isStateful()) {
            foreground.setState(getDrawableState());
        }
    }

    public Drawable getForeground() {
        return foreground;
    }

    public void setForeground(Drawable drawable) {
        if (foreground != drawable) {
            if (foreground != null) {
                foreground.setCallback(null);
                unscheduleDrawable(foreground);
            }

            foreground = drawable;

            if (foreground != null) {
                foreground.setBounds(getLeft(), getTop(), getRight(), getBottom());
                setWillNotDraw(false);
                foreground.setCallback(this);
                if (foreground.isStateful()) {
                    foreground.setState(getDrawableState());
                }
            } else {
                setWillNotDraw(true);
            }
            invalidate();
        }
    }

    @Override public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        if (foreground != null) {
            foreground.draw(canvas);
        }
    }

    @Override public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        if (foreground != null) {
            foreground.setHotspot(x, y);
        }
    }
}