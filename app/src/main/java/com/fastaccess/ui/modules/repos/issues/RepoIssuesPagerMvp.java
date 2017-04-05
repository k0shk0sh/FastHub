package com.fastaccess.ui.modules.repos.issues;

import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;

/**
 * Created by Kosh on 31 Dec 2016, 1:35 AM
 */

public interface RepoIssuesPagerMvp {

    interface View extends BaseMvp.FAView, RepoPagerMvp.TabsBadgeListener {
        void onAddIssue();

        void setCurrentItem(int index, boolean refresh);
    }

    interface Presenter extends BaseMvp.FAPresenter {}

}
