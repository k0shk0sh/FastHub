package com.fastaccess.ui.modules.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.main.MainActivity;
import com.fastaccess.ui.modules.settings.LanguageBottomSheetDialog;
import com.fastaccess.ui.modules.settings.SlackBottomSheetDialog;
import com.fastaccess.ui.widgets.FontEditText;

import java.util.Arrays;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.Optional;
import es.dmoral.toasty.Toasty;
import icepick.State;

/**
 * Created by Kosh on 08 Feb 2017, 9:10 PM
 */

public class LoginActivity extends BaseActivity<LoginMvp.View, LoginPresenter> implements LoginMvp.View {

    @Nullable @BindView(R.id.language_selector) RelativeLayout language_selector;
    @Nullable @BindView(R.id.usernameEditText) TextInputEditText usernameEditText;
    @Nullable @BindView(R.id.username) TextInputLayout username;
    @Nullable @BindView(R.id.passwordEditText) TextInputEditText passwordEditText;
    @Nullable @BindView(R.id.password) TextInputLayout password;
    @Nullable @BindView(R.id.twoFactor) TextInputLayout twoFactor;
    @Nullable @BindView(R.id.twoFactorEditText) TextInputEditText twoFactorEditText;
    @Nullable @BindView(R.id.login) FloatingActionButton login;
    @Nullable @BindView(R.id.progress) ProgressBar progress;

    @State boolean isBasicAuth;

    public static void start(@NonNull Activity activity, boolean isBasicAuth) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.YES_NO_EXTRA, isBasicAuth)
                .end());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Optional @OnClick(R.id.browserLogin) void onOpenBrowser() {
        Uri uri = getPresenter().getAuthorizationUrl();
        ActivityHelper.login(this, uri);
    }

    @Optional @OnClick(R.id.login) public void onClick() {
        doLogin();
    }

    @Optional @OnEditorAction(R.id.passwordEditText) public boolean onSendPassword() {
        if (twoFactor == null || twoFactorEditText == null) return false;
        if (twoFactor.getVisibility() == View.VISIBLE) {
            twoFactorEditText.requestFocus();
        } else {
            doLogin();
        }
        return true;
    }

    @Optional @OnEditorAction(R.id.twoFactorEditText) public boolean onSend2FA() {
        doLogin();
        return true;
    }

    @Optional @OnClick(R.id.language_selector_clicker) public void onChangeLanguage() {
        showLanguage();
    }

    @Override protected int layout() {
        return R.layout.login_form_layout;
    }

    @Override protected boolean isTransparent() {
        return true;
    }

    @Override protected boolean canBack() {
        return false;
    }

    @Override protected boolean isSecured() {
        return true;
    }

    @NonNull @Override public LoginPresenter providePresenter() {
        return new LoginPresenter();
    }

    @Override public void onEmptyUserName(boolean isEmpty) {
        if (username == null) return;
        username.setError(isEmpty ? getString(R.string.required_field) : null);
    }

    @Override public void onRequire2Fa() {
        Toasty.warning(this, getString(R.string.two_factors_otp_error)).show();
        if (twoFactor == null) return;
        twoFactor.setVisibility(View.VISIBLE);
        hideProgress();
    }

    @Override public void onEmptyPassword(boolean isEmpty) {
        if (password == null) return;
        password.setError(isEmpty ? getString(R.string.required_field) : null);
    }

    @Override public void onSuccessfullyLoggedIn() {
        hideProgress();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(SlackBottomSheetDialog.TAG, true);
        startActivity(intent);
        finishAffinity();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.LoginTheme);
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getExtras() != null) {
                isBasicAuth = getIntent().getExtras().getBoolean(BundleConstant.YES_NO_EXTRA);
            }
        }
        if (password != null) password.setHint(isBasicAuth ? getString(R.string.password) : getString(R.string.access_token));
        if (Arrays.asList(getResources().getStringArray(R.array.languages_array_values)).contains(Locale.getDefault().getLanguage())){
            String language = PrefHelper.getString("app_language");
            PrefHelper.set("app_language", Locale.getDefault().getLanguage());
            if(!BuildConfig.DEBUG)
                language_selector.setVisibility(View.GONE);
            if(!Locale.getDefault().getLanguage().equals(language))
                recreate();
        }
    }

    private void showLanguage() {
        LanguageBottomSheetDialog languageBottomSheetDialog = new LanguageBottomSheetDialog();
        languageBottomSheetDialog.onAttach((Context) this);
        languageBottomSheetDialog.show(getSupportFragmentManager(), "LanguageBottomSheetDialog");
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getPresenter().onHandleAuthIntent(intent);
        setIntent(null);
    }

    @Override protected void onResume() {
        super.onResume();
        getPresenter().onHandleAuthIntent(getIntent());
        setIntent(null);
    }

    @Override public void showErrorMessage(@NonNull String msgRes) {
        hideProgress();
        super.showErrorMessage(msgRes);
    }

    @Override public void showMessage(@StringRes int titleRes, @StringRes int msgRes) {
        hideProgress();
        super.showMessage(titleRes, msgRes);
    }

    @Override public void showMessage(@NonNull String titleRes, @NonNull String msgRes) {
        hideProgress();
        super.showMessage(titleRes, msgRes);
    }

    @Override public void showProgress(@StringRes int resId) {
        if (login == null) return;
        login.hide();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(login.getWindowToken(), 0);
        AnimHelper.animateVisibility(progress, true);
    }

    @Override public void onBackPressed() {
        startActivity(new Intent(this, LoginChooserActivity.class));
    }

    @Override public void hideProgress() {
        if (login == null || progress == null) return;
        progress.setVisibility(View.GONE);
        login.show();
    }

    private void doLogin() {
        if (progress == null || twoFactor == null || username == null || password == null) return;
        if (progress.getVisibility() == View.GONE) {
            getPresenter().login(InputHelper.toString(username),
                    InputHelper.toString(password),
                    InputHelper.toString(twoFactor),
                    isBasicAuth);
        }
    }
}
