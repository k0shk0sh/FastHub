package com.fastaccess.ui.modules.filter.issues;

import android.support.annotation.NonNull;

import com.fastaccess.ui.base.mvp.BaseMvp;

/**
 * Created by Kosh on 09 Apr 2017, 6:19 PM
 */

public interface FilterIssuesActivityMvp {

    interface View extends BaseMvp.FAView {
        void onSetCount(int count, boolean isOpen);
    }

    interface Presenter {
        void onStart(@NonNull String login, @NonNull String repoId);
    }
}
