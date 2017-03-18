package com.fastaccess.ui.modules.login;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.AccessTokenModel;
import com.fastaccess.data.dao.AuthModel;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.helper.InputHelper;
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

class LoginPresenter extends BasePresenter<LoginMvp.View> implements LoginMvp.Presenter {

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
        super.onError(throwable);
    }

    @Override public void onTokenResponse(@Nullable AccessTokenModel modelResponse) {
        if (modelResponse != null) {
            String token = modelResponse.getToken();
            if (!InputHelper.isEmpty(token)) {
                PrefGetter.setToken(token);
                makeRestCall(RestProvider.getUserService().getUser(), this::onUserResponse);
                return;
            }
        }
        sendToView(view -> view.showMessage(R.string.error, R.string.failed_login));
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

    @Override public void login(@NonNull String username, @NonNull String password, @Nullable String twoFactorCode) {
        boolean usernameIsEmpty = InputHelper.isEmpty(username);
        boolean passwordIsEmpty = InputHelper.isEmpty(password);
        if (getView() == null) return;
        getView().onEmptyUserName(usernameIsEmpty);
        getView().onEmptyPassword(passwordIsEmpty);
        if (!usernameIsEmpty && !passwordIsEmpty) {
            String authToken = Credentials.basic(username, password);
            AuthModel authModel = new AuthModel();
            authModel.setScopes(Arrays.asList("user", "repo", "gist", "notifications"));
            authModel.setNote(BuildConfig.APPLICATION_ID);
            authModel.setClientSecret(BuildConfig.GITHUB_SECRET);
            authModel.setClientId(BuildConfig.GITHUB_CLIENT_ID);
            authModel.setNoteUr(BuildConfig.REDIRECT_URL);
            if (!InputHelper.isEmpty(twoFactorCode)) {
                authModel.setOtpCode(twoFactorCode);
            }
            makeRestCall(LoginProvider.getLoginRestService(authToken, twoFactorCode).login(authModel), this::onTokenResponse);
        }
    }
}
