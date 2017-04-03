package com.fastaccess.ui.modules.user;

import android.support.annotation.NonNull;

import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.profile.ProfilePagerMvp;

/**
 * Created by Kosh on 04 Dec 2016, 1:11 PM
 */

public interface UserPagerMvp {

    interface View extends BaseMvp.FAView, ProfilePagerMvp.View {
        void onInitOrg(boolean isMember);
    }

    interface Presenter extends BaseMvp.FAPresenter {

        void checkOrgMembership(@NonNull String org);
    }

}
