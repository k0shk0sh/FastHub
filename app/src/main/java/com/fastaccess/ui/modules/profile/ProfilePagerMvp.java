package com.fastaccess.ui.modules.profile;

import com.fastaccess.ui.base.mvp.BaseMvp;

/**
 * Created by Kosh on 03 Dec 2016, 7:59 AM
 */

public interface ProfilePagerMvp {

    interface View extends BaseMvp.FAView {
        void onNavigateToFollowers();

        void onNavigateToFollowing();

        void onCheckType(boolean isOrg);

    }

    interface Presenter extends BaseMvp.FAPresenter {}
}
