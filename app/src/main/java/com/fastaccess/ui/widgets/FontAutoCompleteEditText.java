package com.fastaccess.ui.widgets;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import com.fastaccess.helper.TypeFaceHelper;
import com.fastaccess.helper.ViewHelper;

/**
 * Created by Kosh on 8/18/2015. copyrights are reserved
 */
public class FontAutoCompleteEditText extends AppCompatAutoCompleteTextView {

    public FontAutoCompleteEditText(Context context) {
        super(context);
        init();
    }

    public FontAutoCompleteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public FontAutoCompleteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (isInEditMode()) return;
        if (isInEditMode()) return;
        setInputType(getInputType() | EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        setImeOptions(getImeOptions() | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        TypeFaceHelper.applyTypeface(this);
    }

    public void setTextColor(@ColorRes int normalColor, @ColorRes int pressedColor) {
        int nColor = ContextCompat.getColor(getContext(), normalColor);
        int pColor = ContextCompat.getColor(getContext(), pressedColor);
        setTextColor(ViewHelper.textSelector(nColor, pColor));
    }
}
