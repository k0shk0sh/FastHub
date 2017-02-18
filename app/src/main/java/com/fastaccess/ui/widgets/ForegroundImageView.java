package com.fastaccess.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Toast;

import com.fastaccess.R;
import com.fastaccess.helper.ViewHelper;


public class ForegroundImageView extends AppCompatImageView {
    private Drawable foreground;
    private Toast toast;

    public ForegroundImageView(Context context) {
        this(context, null);
    }

    public ForegroundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setOnLongClickListener(view -> {
            if (toast != null) toast.cancel();
            toast = Toast.makeText(getContext(), getContentDescription(), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return true;
        });
    }

    public ForegroundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ForegroundImageView);
        Drawable foreground = a.getDrawable(R.styleable.ForegroundImageView_android_foreground);
        if (foreground != null) {
            setForeground(foreground);
        }
        a.recycle();
    }

    public void setForegroundResource(@DrawableRes int drawableResId) {
        if (isInEditMode()) return;
        setForeground(ContextCompat.getDrawable(getContext(), drawableResId));
    }

    public void setForeground(Drawable drawable) {
        if (foreground == drawable) {
            return;
        }
        if (foreground != null) {
            foreground.setCallback(null);
            unscheduleDrawable(foreground);
        }

        foreground = drawable;

        if (drawable != null) {
            drawable.setCallback(this);
            if (drawable.isStateful()) {
                drawable.setState(getDrawableState());
            }
        }
        requestLayout();
        invalidate();
    }

    @Override protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || who == foreground;
    }

    @Override public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (foreground != null) foreground.jumpToCurrentState();
    }

    @Override protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (foreground != null && foreground.isStateful()) {
            foreground.setState(getDrawableState());
        }
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (foreground != null) {
            foreground.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            invalidate();
        }
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (foreground != null) {
            foreground.setBounds(0, 0, w, h);
            invalidate();
        }
    }

    @Override public void draw(Canvas canvas) {
        super.draw(canvas);

        if (foreground != null) {
            foreground.draw(canvas);
        }
    }

    public void tintDrawable(@ColorRes int colorRes) {
        tintDrawableFromColor(ContextCompat.getColor(getContext(), colorRes));
    }

    public void tintDrawableFromColor(@ColorInt int color) {
        ViewHelper.tintDrawable(getDrawable(), color);
    }
}