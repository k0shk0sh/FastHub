package com.fastaccess.ui.modules.login;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.AccessTokenModel;
import com.fastaccess.data.dao.AuthModel;
import com.fastaccess.data.dao.LoginModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.rest.LoginProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.Arrays;

import java.util.UUID;
import okhttp3.Credentials;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;

/**
 * Created by Kosh on 09 Nov 2016, 9:43 PM
 */

class LoginPresenter extends BasePresenter<LoginMvp.View> implements LoginMvp.Presenter {

    @Override public void onGetToken(@NonNull String code) {
        makeRestCall(RestProvider.getLoginRestService().getAccessToken(code,
                BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_SECRET,
                BuildConfig.APPLICATION_ID, BuildConfig.REDIRECT_URL),
                this::onTokenResponse);
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

    @Override public void onUserResponse(@Nullable LoginModel userModel) {
        if (userModel != null) {
            userModel.setToken(PrefGetter.getToken());
            userModel.save();
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
            authModel.setNote(BuildConfig.APPLICATION_ID + "-" + authToken);//make it unique to FastHub.
            authModel.setClientSecret(BuildConfig.GITHUB_SECRET);

            UUID uuid = UUID.randomUUID();
            String fingerprint = BuildConfig.APPLICATION_ID + " - " + uuid;

            Observable<AccessTokenModel> loginCall =
                LoginProvider.getLoginRestService(authToken).login(BuildConfig.GITHUB_CLIENT_ID,
                    fingerprint,
                    authModel);

            if (twoFactorCode != null && !twoFactorCode.isEmpty()) {
                loginCall = LoginProvider.getLoginRestService(authToken).login(BuildConfig.GITHUB_CLIENT_ID,
                    fingerprint,
                    authModel,
                    twoFactorCode);
            }

            makeRestCall(loginCall, tokenModel -> {
                if (InputHelper.isEmpty(tokenModel.getToken())) {
                    makeRestCall(LoginProvider.getLoginRestService(authToken).deleteToken(tokenModel.getId()),
                            response -> login(username, password, null));
                } else {
                    onTokenResponse(tokenModel);
                }
            });
        }
    }

    @Override
    public void onError(@NonNull Throwable throwable) {
        if (RestProvider.getErrorCode(throwable) == 401) {
            String twoFaToken = ((HttpException) throwable).response().headers().get("X-GitHub-OTP");
            if (twoFaToken != null) {
                sendToView(LoginMvp.View::onRequire2Fa);
            } else {
                sendToView(LoginMvp.View::onRequireLogin);
            }
            return;
        } else {
            super.onError(throwable);
        }
    }
}
