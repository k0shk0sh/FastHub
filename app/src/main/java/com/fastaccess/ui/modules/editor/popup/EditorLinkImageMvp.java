package com.fastaccess.ui.modules.editor.popup;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.ui.base.mvp.BaseMvp;

import java.io.File;

/**
 * Created by Kosh on 15 Apr 2017, 9:06 PM
 */

public interface EditorLinkImageMvp {

    interface EditorLinkCallback {
        void onAppendLink(@Nullable String title, @Nullable String link, boolean isLink);
    }

    interface View extends BaseMvp.FAView {
        void onUploaded(@Nullable String title, @Nullable String link);
    }

    interface Presenter {
        void onSubmit(@Nullable String title, @NonNull File file);
    }
}
