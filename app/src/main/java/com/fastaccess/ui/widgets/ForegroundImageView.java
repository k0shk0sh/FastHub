package com.fastaccess.ui.widgets;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Toast;

import com.fastaccess.helper.ViewHelper;


public class ForegroundImageView extends AppCompatImageView {
    private Toast toast;


    public ForegroundImageView(@NonNull Context context) {
        this(context, null);
    }

    public ForegroundImageView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        if (getContentDescription() != null) {
            setOnLongClickListener(view -> {
                if (toast != null) toast.cancel();
                toast = Toast.makeText(getContext(), getContentDescription(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return true;
            });
        }
    }

    public ForegroundImageView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void tintDrawableColor(@ColorInt int color) {
        ViewHelper.tintDrawable(getDrawable(), color);
    }
}