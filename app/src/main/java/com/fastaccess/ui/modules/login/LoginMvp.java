package com.fastaccess.ui.modules.login;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.AccessTokenModel;
import com.fastaccess.data.dao.LoginModel;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;

/**
 * Created by Kosh on 09 Nov 2016, 9:41 PM
 */

interface LoginMvp {

    interface View extends BaseMvp.FAView, AppbarRefreshLayout.OnRefreshListener {
        void onSuccessfullyLoggedIn();
    }

    interface Presenter extends BaseMvp.FAPresenter {

        @Nullable String getCode(@NonNull String url);

        @NonNull Uri getAuthorizationUrl();

        void onGetToken(@NonNull String code);

        void onTokenResponse(@Nullable AccessTokenModel response);

        void onUserResponse(@Nullable LoginModel response);
    }
}
