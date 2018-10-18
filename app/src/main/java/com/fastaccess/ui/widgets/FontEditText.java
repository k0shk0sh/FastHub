package com.fastaccess.ui.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import com.crashlytics.android.Crashlytics;
import com.fastaccess.helper.TypeFaceHelper;

/**
 * Created by Kosh on 8/18/2015. copyrights are reserved
 */
public class FontEditText extends AppCompatEditText {

    public FontEditText(@NonNull Context context) {
        super(context);
        init();
    }

    public FontEditText(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public FontEditText(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (isInEditMode()) return;
        setInputType(getInputType() | EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        setImeOptions(getImeOptions() | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        TypeFaceHelper.applyTypeface(this);
    }

    @SuppressLint("SetTextI18n") public void setText(CharSequence text, BufferType type) {
        try {
            super.setText(text, type);
        } catch (Exception e) {
            setText("I tried, but your OEM just sucks because they modify the framework components and therefore causing the app to crash!" + ".\nFastHub");
            Crashlytics.logException(e);
        }
    }
}
