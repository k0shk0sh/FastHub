package com.fastaccess.ui.modules.repos.issues;

import android.support.annotation.IntRange;

import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;

/**
 * Created by Kosh on 31 Dec 2016, 1:35 AM
 */

public interface RepoIssuesPagerMvp {

    interface View extends BaseMvp.FAView, RepoPagerMvp.TabsBadgeListener {
        void onAddIssue();

        void setCurrentItem(int index, boolean refresh);

        void onChangeIssueSort(boolean isLastUpdated);

        @IntRange(from = 0, to = 1) int getCurrentItem();

        void onScrolled(boolean isUp);
    }

    interface Presenter extends BaseMvp.FAPresenter {}

}
