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
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.Arrays;

import okhttp3.Credentials;
import retrofit2.HttpException;

/**
 * Created by Kosh on 09 Nov 2016, 9:43 PM
 */

public class LoginPresenter extends BasePresenter<LoginMvp.View> implements LoginMvp.Presenter {

    LoginPresenter() {
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
                makeRestCall(RestProvider.getUserService(false).getUser(), login -> onUserResponse(login, false));
                return;
            }
        }
        sendToView(view -> view.showMessage(R.string.error, R.string.failed_login));
    }

    @NonNull @Override public Uri getAuthorizationUrl(@Nullable String endpoint) {
        Uri.Builder builder = new Uri.Builder();
        if (!InputHelper.isEmpty(endpoint)) {
            endpoint = LinkParserHelper.getEndpoint(endpoint);
            Uri uri = Uri.parse(endpoint);
            if (uri.getScheme() != null && uri.getAuthority() != null) {
                builder.scheme(uri.getScheme())
                        .authority(uri.getAuthority());
            } else {
                throw new IllegalArgumentException("Uri is invalid: " + endpoint);
            }
        } else {
            builder.scheme("https")
                    .authority("github.com");
        }
        return builder.appendPath("login")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("client_id", GithubConfigHelper.getClientId())
                .appendQueryParameter("redirect_uri", GithubConfigHelper.getRedirectUrl())
                .appendQueryParameter("scope", "user,repo,gist,notifications,read:org")
                .appendQueryParameter("state", BuildConfig.APPLICATION_ID)
                .build();
    }

    @Override public void onHandleAuthIntent(@Nullable Intent intent, boolean extraLogin) {
        Logger.e(intent, intent != null ? intent.getExtras() : "N/A");
        if (intent != null && intent.getData() != null) {
            Uri uri = intent.getData();
            Logger.e(uri.toString(), extraLogin);
            if (uri.toString().startsWith(GithubConfigHelper.getRedirectUrl())) {
                String tokenCode = uri.getQueryParameter("code");
                if (!InputHelper.isEmpty(tokenCode)) {
                    makeRestCall(LoginProvider.getLoginRestService().getAccessToken(tokenCode, GithubConfigHelper.getClientId(),
                            GithubConfigHelper.getSecret(), BuildConfig.APPLICATION_ID, GithubConfigHelper.getRedirectUrl()),
                            modelResponse -> {
                                if (extraLogin) {
                                    String token = modelResponse.getToken() != null ? modelResponse.getToken() : modelResponse.getAccessToken();
                                    if (!InputHelper.isEmpty(token)) {
                                        PrefGetter.setToken(token);
                                        sendToView(view -> view.onSuccessfullyLoggedIn(false));
                                        return;
                                    }
                                }
                                onTokenResponse(modelResponse);
                            });
                } else {
                    sendToView(view -> view.showMessage(R.string.error, R.string.error));
                }
            }
        }
    }

    @Override public void onUserResponse(@Nullable Login userModel, boolean isEnterprise) {
        if (userModel != null) {
            Logger.e(isEnterprise, PrefGetter.getEnterpriseToken(), PrefGetter.getToken());
            userModel.setToken(isEnterprise ? PrefGetter.getEnterpriseToken() : PrefGetter.getToken());
            userModel.save(userModel);
            sendToView(view -> view.onSuccessfullyLoggedIn(isEnterprise));
            return;
        }
        sendToView(view -> view.showMessage(R.string.error, R.string.failed_login));
    }

    @Override public void login(@NonNull String username, @NonNull String password,
                                @Nullable String twoFactorCode, boolean isBasicAuth,
                                @Nullable String endpoint, boolean isEnterprise) {
        setEnterprise(isEnterprise);
        boolean usernameIsEmpty = InputHelper.isEmpty(username);
        boolean passwordIsEmpty = InputHelper.isEmpty(password);
        boolean endpointIsEmpty = InputHelper.isEmpty(endpoint) && isEnterprise;
        if (getView() == null) return;
        getView().onEmptyUserName(usernameIsEmpty);
        getView().onEmptyPassword(passwordIsEmpty);
        getView().onEmptyEndpoint(endpointIsEmpty);
        if ((!usernameIsEmpty && !passwordIsEmpty)) {
            String authToken = Credentials.basic(username, password);
            if (isBasicAuth && !isEnterprise) {
                AuthModel authModel = new AuthModel();
                authModel.setScopes(Arrays.asList("user", "repo", "gist", "notifications", "read:org"));
                authModel.setNote(BuildConfig.APPLICATION_ID);
                authModel.setClientSecret(GithubConfigHelper.getSecret());
                authModel.setClientId(GithubConfigHelper.getClientId());
                authModel.setNoteUr(GithubConfigHelper.getRedirectUrl());
                if (!InputHelper.isEmpty(twoFactorCode)) {
                    authModel.setOtpCode(twoFactorCode);
                }
                makeRestCall(LoginProvider.getLoginRestService(authToken, twoFactorCode, null).login(authModel), accessTokenModel -> {
                    if (!InputHelper.isEmpty(twoFactorCode)) {
                        PrefGetter.setOtpCode(twoFactorCode);
                    }
                    onTokenResponse(accessTokenModel);
                });
            } else {
                accessTokenLogin(password, endpoint, twoFactorCode, authToken, isEnterprise);
            }
        }
    }

    private void accessTokenLogin(@NonNull String password, @Nullable String endpoint, @Nullable String otp, String authToken, boolean isEnterprise) {
        makeRestCall(LoginProvider.getLoginRestService(authToken, otp, endpoint).loginAccessToken(), login -> {
            if (!isEnterprise) {
                PrefGetter.setToken(password);
            } else {
                if (!InputHelper.isEmpty(otp)) {
                    PrefGetter.setEnterpriseOtpCode(otp);
                }
                PrefGetter.setTokenEnterprise(authToken);
                if (!InputHelper.isEmpty(endpoint)) {
                    PrefGetter.setEnterpriseUrl(endpoint);
                }
            }
            onUserResponse(login, isEnterprise);
        });
    }
}
