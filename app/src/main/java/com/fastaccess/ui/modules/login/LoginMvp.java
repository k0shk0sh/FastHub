package com.fastaccess.ui.modules.login;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.AccessTokenModel;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.ui.base.mvp.BaseMvp;

/**
 * Created by Kosh on 09 Nov 2016, 9:41 PM
 */

public interface LoginMvp {

    interface View extends BaseMvp.FAView {

        void onRequire2Fa();

        void onEmptyUserName(boolean isEmpty);

        void onEmptyPassword(boolean isEmpty);

        void onEmptyEndpoint(boolean isEmpty);

        void onSuccessfullyLoggedIn(boolean extraLogin);
    }

    interface Presenter extends BaseMvp.FAPresenter {

        @NonNull Uri getAuthorizationUrl();

        void onHandleAuthIntent(@Nullable Intent intent);

        void onTokenResponse(@Nullable AccessTokenModel response);

        void onUserResponse(@Nullable Login response);

        void login(@NonNull String username, @NonNull String password,
                   @Nullable String twoFactorCode, boolean isBasicAuth,
                   @Nullable String endpoint);
    }
}
