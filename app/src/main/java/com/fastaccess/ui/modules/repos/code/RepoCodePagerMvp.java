package com.fastaccess.ui.modules.repos.code;

import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;

/**
 * Created by Kosh on 31 Dec 2016, 1:35 AM
 */

public interface RepoCodePagerMvp {

    interface View extends BaseMvp.FAView, RepoPagerMvp.TabsBadgeListener {
        boolean canPressBack();

        void onBackPressed();
    }

    interface Presenter extends BaseMvp.FAPresenter {}
}
