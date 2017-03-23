package com.fastaccess.ui.modules.repos.pull_requests;

import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;

/**
 * Created by Kosh on 31 Dec 2016, 1:35 AM
 */

interface RepoPullRequestPagerMvp {

    interface View extends BaseMvp.FAView, RepoPagerMvp.TabsBadgeListener {}

    interface Presenter extends BaseMvp.FAPresenter {}
}
