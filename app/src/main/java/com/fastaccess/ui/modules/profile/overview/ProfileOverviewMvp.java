package com.fastaccess.ui.modules.profile.overview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.ui.base.mvp.BaseMvp;

/**
 * Created by Kosh on 03 Dec 2016, 9:15 AM
 */

interface ProfileOverviewMvp {

    interface View extends BaseMvp.FAView {
        void onInitViews(@Nullable User userModel);

        void onInvalidateMenuItem();
    }

    interface Presenter extends BaseMvp.FAPresenter {

        void onFragmentCreated(@Nullable Bundle bundle);

        void onWorkOffline(@NonNull String login);

        void onCheckFollowStatus(@NonNull String login);

        boolean isSuccessResponse();

        boolean isFollowing();

        void onFollowButtonClicked(@NonNull String login);

        void onSendUserToView(@Nullable User userModel);

        @NonNull String getLogin();
    }
}
