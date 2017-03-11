package com.fastaccess.ui.modules.user;

import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.profile.ProfilePagerMvp;

/**
 * Created by Kosh on 04 Dec 2016, 1:11 PM
 */

public interface UserPagerMvp {

    interface View extends BaseMvp.FAView, ProfilePagerMvp.View {}

    interface Presenter extends BaseMvp.FAPresenter {}

}
