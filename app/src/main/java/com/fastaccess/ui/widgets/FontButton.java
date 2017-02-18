package com.fastaccess.ui.widgets;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.fastaccess.helper.TypeFaceHelper;
import com.fastaccess.helper.ViewHelper;


/**
 * Created by Kosh on 8/18/2015. copyrights are reserved
 */
public class FontButton extends AppCompatButton {

    public FontButton(Context context) {
        super(context);
        init();
    }

    public FontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (isInEditMode()) return;
        TypeFaceHelper.applyTypeface(this);
    }

    public void setBackground(@ColorRes int normalColor, @ColorRes int pressedColor) {
        int nColor = ContextCompat.getColor(getContext(), normalColor);
        int pColor = ContextCompat.getColor(getContext(), pressedColor);
        setBackgroundDrawable(ViewHelper.getDrawableSelector(nColor, pColor));
    }

    public void setTextColor(@ColorRes int normalColor, @ColorRes int pressedColor) {
        int nColor = ContextCompat.getColor(getContext(), normalColor);
        int pColor = ContextCompat.getColor(getContext(), pressedColor);
        setTextColor(ViewHelper.textSelector(nColor, pColor));
    }

}