package com.fastaccess.ui.modules.repos.code;

import android.support.annotation.NonNull;

import com.fastaccess.ui.base.mvp.BaseMvp;

/**
 * Created by Kosh on 31 Dec 2016, 1:35 AM
 */

public interface RepoCodePagerMvp {

    interface View extends BaseMvp.FAView {
        boolean canPressBack();

        void onBackPressed();

        void onBranchChanged(@NonNull String branch);
    }

    interface Presenter extends BaseMvp.FAPresenter {}
}
