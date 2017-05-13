package com.fastaccess.ui.modules.login;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.AccessTokenModel;
import com.fastaccess.data.dao.AuthModel;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.helper.GithubConfigHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.rest.LoginProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.Arrays;

import okhttp3.Credentials;
import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by Kosh on 09 Nov 2016, 9:43 PM
 */

public class LoginPresenter extends BasePresenter<LoginMvp.View> implements LoginMvp.Presenter {

    public LoginPresenter() {
        RestProvider.clearHttpClient();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
    }

    @Override public void onError(@NonNull Throwable throwable) {
        if (RestProvider.getErrorCode(throwable) == 401 && throwable instanceof HttpException) {
            retrofit2.Response response = ((HttpException) throwable).response();
            if (response != null && response.headers() != null) {
                String twoFaToken = response.headers().get("X-GitHub-OTP");
                if (twoFaToken != null) {
                    sendToView(LoginMvp.View::onRequire2Fa);
                    return;
                } else {
                    sendToView(view -> view.showMessage(R.string.error, R.string.failed_login));
                    return;
                }
            }
        }
        sendToView(view -> view.showErrorMessage(throwable.getMessage()));
    }

    @Override public void onTokenResponse(@Nullable AccessTokenModel modelResponse) {
        if (modelResponse != null) {
            String token = modelResponse.getToken() != null ? modelResponse.getToken() : modelResponse.getAccessToken();
            if (!InputHelper.isEmpty(token)) {
                PrefGetter.setToken(token);
                makeRestCall(RestProvider.getUserService().getUser(), this::onUserResponse);
                return;
            }
        }
        sendToView(view -> view.showMessage(R.string.error, R.string.failed_login));
    }

    @NonNull @Override public Uri getAuthorizationUrl() {
        return new Uri.Builder()
                .scheme("https")
                .authority("github.com")
                .appendPath("login")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", GithubConfigHelper.getClientId())
                .appendQueryParameter("redirect_uri", GithubConfigHelper.getRedirectUrl())
                .appendQueryParameter("scope", "user,repo,gist,notifications,read:org")
                .appendQueryParameter("state", BuildConfig.APPLICATION_ID)
                .build();
    }

    @Override public void onHandleAuthIntent(@Nullable Intent intent) {
        Logger.e(intent, intent != null ? intent.getExtras() : "N/A");
        if (intent != null && intent.getData() != null) {
            Uri uri = intent.getData();
            Logger.e(uri.toString());
            if (uri.toString().startsWith(GithubConfigHelper.getRedirectUrl())) {
                String tokenCode = uri.getQueryParameter("code");
                if (!InputHelper.isEmpty(tokenCode)) {
                    makeRestCall(LoginProvider.getLoginRestService().getAccessToken(tokenCode, GithubConfigHelper.getClientId(),
                            GithubConfigHelper.getSecret(), BuildConfig.APPLICATION_ID, GithubConfigHelper.getRedirectUrl()), this::onTokenResponse);
                } else {
                    sendToView(view -> view.showMessage(R.string.error, R.string.error));
                }
            }
        }
    }

    @Override public void onUserResponse(@Nullable Login userModel) {
        if (userModel != null) {
            userModel.setToken(PrefGetter.getToken());
            userModel.save(userModel);
            sendToView(LoginMvp.View::onSuccessfullyLoggedIn);
            return;
        }
        sendToView(view -> view.showMessage(R.string.error, R.string.failed_login));
    }

    @Override public void login(@NonNull String username, @NonNull String password,
                                @Nullable String twoFactorCode, boolean isBasicAuth) {
        boolean usernameIsEmpty = InputHelper.isEmpty(username);
        boolean passwordIsEmpty = InputHelper.isEmpty(password);
        if (getView() == null) return;
        getView().onEmptyUserName(usernameIsEmpty);
        getView().onEmptyPassword(passwordIsEmpty);
        if (!usernameIsEmpty && !passwordIsEmpty) {
            String authToken = Credentials.basic(username, password);
            if (isBasicAuth) {
                AuthModel authModel = new AuthModel();
                authModel.setScopes(Arrays.asList("user", "repo", "gist", "notifications", "read:org"));
                authModel.setNote(BuildConfig.APPLICATION_ID);
                authModel.setClientSecret(GithubConfigHelper.getSecret());
                authModel.setClientId(GithubConfigHelper.getClientId());
                authModel.setNoteUr(GithubConfigHelper.getRedirectUrl());
                if (!InputHelper.isEmpty(twoFactorCode)) {
                    authModel.setOtpCode(twoFactorCode);
                }
                makeRestCall(LoginProvider.getLoginRestService(authToken, twoFactorCode).login(authModel), accessTokenModel -> {
                    if (!InputHelper.isEmpty(twoFactorCode)) {
                        PrefGetter.setOtpCode(twoFactorCode);
                    }
                    onTokenResponse(accessTokenModel);
                });
            } else {
                makeRestCall(LoginProvider.getLoginRestService(authToken, null).loginAccessToken(), login -> {
                    if (login != null) {
                        PrefGetter.setToken(InputHelper.toString(password));
                    }
                    onUserResponse(login);
                });
            }
        }
    }
}
