package com.fastaccess.ui.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.fastaccess.helper.TypeFaceHelper;


/**
 * Created by Kosh on 8/18/2015. copyrights are reserved
 */
public class FontCheckbox extends AppCompatCheckBox {

    public FontCheckbox(@NonNull Context context) {
        super(context);
        init();
    }

    public FontCheckbox(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontCheckbox(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (isInEditMode()) return;
        TypeFaceHelper.applyTypeface(this);
    }
}
