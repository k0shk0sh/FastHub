package com.fastaccess.ui.modules.changelog;

import android.support.annotation.Nullable;

import com.fastaccess.ui.base.mvp.BaseMvp;

/**
 * Created by Kosh on 28 May 2017, 10:53 AM
 */

public interface ChangelogMvp {

    interface View extends BaseMvp.FAView {
        void onChangelogLoaded(@Nullable String html);
    }

    interface Presenter {
        void onLoadChangelog();
    }
}
