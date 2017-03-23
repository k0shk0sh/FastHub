package com.fastaccess.ui.widgets;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

import com.fastaccess.helper.TypeFaceHelper;


/**
 * Created by Kosh on 8/18/2015. copyrights are reserved
 */
public class FontRadioButton extends AppCompatRadioButton {

    public FontRadioButton(Context context) {
        super(context);
        init();
    }

    public FontRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (isInEditMode()) return;
        TypeFaceHelper.applyTypeface(this);
    }
}
