package com.fastaccess.ui.modules.login;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.AccessTokenModel;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.modules.settings.SlackBottomSheetDialog;

/**
 * Created by Kosh on 09 Nov 2016, 9:41 PM
 */

interface LoginMvp {

    interface View extends BaseMvp.FAView, SlackBottomSheetDialog.SlackDialogListener {

        void onRequire2Fa();

        void onEmptyUserName(boolean isEmpty);

        void onEmptyPassword(boolean isEmpty);

        void onSuccessfullyLoggedIn();
    }

    interface Presenter extends BaseMvp.FAPresenter {

        @NonNull Uri getAuthorizationUrl();

        void onHandleAuthIntent(@Nullable Intent intent);

        void onTokenResponse(@Nullable AccessTokenModel response);

        void onUserResponse(@Nullable Login response);

        void login(@NonNull String username, @NonNull String password, @Nullable String twoFactorCode, boolean isBasicAuth);
    }
}
