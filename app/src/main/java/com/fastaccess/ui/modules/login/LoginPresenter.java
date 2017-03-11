package com.fastaccess.ui.modules.login;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.AccessTokenModel;
import com.fastaccess.data.dao.LoginModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by Kosh on 09 Nov 2016, 9:43 PM
 */

class LoginPresenter extends BasePresenter<LoginMvp.View> implements LoginMvp.Presenter {

    @Nullable @Override public String getCode(@NonNull String url) {
        Uri uri = Uri.parse(url);
        if (uri != null && uri.toString().startsWith(BuildConfig.REDIRECT_URL)) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                return code;
            } else if (uri.getQueryParameter("error") != null) {
                sendToView(view -> view.showMessage(R.string.error, R.string.failed_login));
            }
        }
        return null;
    }

    @NonNull @Override public Uri getAuthorizationUrl() {
        return new Uri.Builder()
                .scheme("https")
                .authority("github.com")
                .appendPath("login")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
                .appendQueryParameter("redirect_uri", BuildConfig.REDIRECT_URL)
                .appendQueryParameter("scope", "user,repo,gist,notifications")
                .appendQueryParameter("state", BuildConfig.APPLICATION_ID)
                .build();
    }

    @Override public void onGetToken(@NonNull String code) {
        makeRestCall(RestProvider.getLoginRestService().getAccessToken(code,
                BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_SECRET,
                BuildConfig.APPLICATION_ID, BuildConfig.REDIRECT_URL),
                this::onTokenResponse);
    }

    @Override public void onTokenResponse(@Nullable AccessTokenModel modelResponse) {
        if (modelResponse != null) {
            String token = modelResponse.getAccessToken();
            if (!InputHelper.isEmpty(token)) {
                PrefGetter.setToken(token);
                makeRestCall(RestProvider.getUserService().getUser(), this::onUserResponse);
                return;
            }
        }
        sendToView(view -> view.showMessage(R.string.error, R.string.failed_login));
    }

    @Override public void onUserResponse(@Nullable LoginModel userModel) {
        if (userModel != null) {
            userModel.setToken(PrefGetter.getToken());
            userModel.save();
            sendToView(LoginMvp.View::onSuccessfullyLoggedIn);
            return;
        }
        sendToView(view -> view.showMessage(R.string.error, R.string.failed_login));
    }
}
