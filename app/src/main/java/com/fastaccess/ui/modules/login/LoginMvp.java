package com.fastaccess.ui.modules.login;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.AccessTokenModel;
import com.fastaccess.data.dao.LoginModel;
import com.fastaccess.ui.base.mvp.BaseMvp;

/**
 * Created by Kosh on 09 Nov 2016, 9:41 PM
 */

interface LoginMvp {

    interface View extends BaseMvp.FAView {

        void onRequire2Fa();

        void onEmptyUserName(boolean isEmpty);

        void onEmptyPassword(boolean isEmpty);

        void onSuccessfullyLoggedIn();
    }

    interface Presenter extends BaseMvp.FAPresenter {

        void onTokenResponse(@Nullable AccessTokenModel response);

        void onUserResponse(@Nullable LoginModel response);

        void login(@NonNull String username, @NonNull String password, @Nullable String twoFactorCode);
    }
}
