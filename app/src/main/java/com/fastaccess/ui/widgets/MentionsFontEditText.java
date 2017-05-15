package com.fastaccess.ui.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import com.fastaccess.helper.TypeFaceHelper;
import com.linkedin.android.spyglass.ui.MentionsEditText;

/**
 * Created by JediB on 5/15/2017.
 */

public class MentionsFontEditText extends MentionsEditText {

    public MentionsFontEditText(@NonNull Context context) {
        super(context);
        init();
    }

    public MentionsFontEditText(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public MentionsFontEditText(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (isInEditMode()) return;
        setInputType(getInputType() | EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        setImeOptions(getImeOptions() | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        TypeFaceHelper.applyTypeface(this);
    }
}
