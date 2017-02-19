package com.fastaccess.ui.modules.repos.issues;

import com.fastaccess.ui.base.mvp.BaseMvp;

/**
 * Created by Kosh on 31 Dec 2016, 1:35 AM
 */

interface RepoIssuesPagerMvp {

    interface View extends BaseMvp.FAView {
        void onAddIssue();
    }

    interface Presenter extends BaseMvp.FAPresenter {}
}
