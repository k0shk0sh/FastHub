package com.fastaccess.ui.modules.user;

import android.support.annotation.NonNull;

import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.profile.ProfilePagerMvp;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;

/**
 * Created by Kosh on 04 Dec 2016, 1:11 PM
 */

public interface UserPagerMvp {

    interface View extends BaseMvp.FAView, ProfilePagerMvp.View, RepoPagerMvp.TabsBadgeListener {
        void onInitOrg(boolean isMember);

        void onUserBlocked();

        void onInvalidateMenu();

        void onUserUnBlocked();
    }

    interface Presenter extends BaseMvp.FAPresenter {

        void onCheckBlocking(@NonNull String login);

        void checkOrgMembership(@NonNull String org);

        void onBlockUser(@NonNull String login);

        void onUnblockUser(@NonNull String login);
    }

}
