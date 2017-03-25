package com.fastaccess.ui.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;

import com.fastaccess.helper.TypeFaceHelper;


/**
 * Created by Kosh on 8/18/2015. copyrights are reserved
 */
public class FontSwitchView extends SwitchCompat {

    public FontSwitchView(@NonNull Context context) {
        super(context);
        init();
    }

    public FontSwitchView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public FontSwitchView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (isInEditMode()) return;
        TypeFaceHelper.applyTypeface(this);
    }
}
