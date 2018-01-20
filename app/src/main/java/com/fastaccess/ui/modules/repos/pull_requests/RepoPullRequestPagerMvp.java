package com.fastaccess.ui.modules.repos.pull_requests;

import android.support.annotation.IntRange;

import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;

/**
 * Created by Kosh on 31 Dec 2016, 1:35 AM
 */

public interface RepoPullRequestPagerMvp {

    interface View extends BaseMvp.FAView, RepoPagerMvp.TabsBadgeListener {
        @IntRange(from = 0, to = 1) int getCurrentItem();

        void onScrolled(boolean isUp);
    }

    interface Presenter extends BaseMvp.FAPresenter {}
}
