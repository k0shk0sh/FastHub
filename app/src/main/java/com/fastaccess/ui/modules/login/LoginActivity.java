package com.fastaccess.ui.modules.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.ProgressBar;

import com.evernote.android.state.State;
import com.fastaccess.App;
import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.login.chooser.LoginChooserActivity;
import com.fastaccess.ui.modules.main.donation.DonateActivity;
import com.fastaccess.ui.widgets.FontCheckbox;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.miguelbcr.io.rx_billing_service.RxBillingService;
import com.miguelbcr.io.rx_billing_service.entities.ProductType;
import com.miguelbcr.io.rx_billing_service.entities.Purchase;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import es.dmoral.toasty.Toasty;
import io.reactivex.functions.Action;

/**
 * Created by Kosh on 08 Feb 2017, 9:10 PM
 */

public class LoginActivity extends BaseActivity<LoginMvp.View, LoginPresenter> implements LoginMvp.View {

    @BindView(R.id.usernameEditText) TextInputEditText usernameEditText;
    @BindView(R.id.username) TextInputLayout username;
    @BindView(R.id.passwordEditText) TextInputEditText passwordEditText;
    @BindView(R.id.password) TextInputLayout password;
    @BindView(R.id.twoFactor) TextInputLayout twoFactor;
    @BindView(R.id.twoFactorEditText) TextInputEditText twoFactorEditText;
    @BindView(R.id.login) FloatingActionButton login;
    @BindView(R.id.progress) ProgressBar progress;
    @BindView(R.id.accessTokenCheckbox) FontCheckbox accessTokenCheckbox;
    @BindView(R.id.endpoint) TextInputLayout endpoint;
    @State boolean isBasicAuth;

    public static void startOAuth(@NonNull Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.YES_NO_EXTRA, true)
                .put(BundleConstant.EXTRA_TWO, true)
                .end());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void start(@NonNull Activity activity, boolean isBasicAuth) {
        start(activity, isBasicAuth, false);
    }

    public static void start(@NonNull Activity activity, boolean isBasicAuth, boolean isEnterprise) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.YES_NO_EXTRA, isBasicAuth)
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .end());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @OnClick(R.id.browserLogin) void onOpenBrowser() {
        if (isEnterprise()) {
            MessageDialogView.newInstance(getString(R.string.warning), getString(R.string.github_enterprise_reply),
                    true, Bundler.start().put("hide_buttons", true).end())
                    .show(getSupportFragmentManager(), MessageDialogView.TAG);
            return;
        }
        ActivityHelper.startCustomTab(this, getPresenter().getAuthorizationUrl());
    }

    @OnClick(R.id.login) public void onClick() {
        doLogin();
    }

    @OnCheckedChanged(R.id.accessTokenCheckbox) void onCheckChanged(boolean checked) {
        isBasicAuth = !checked;
        password.setHint(checked ? getString(R.string.access_token) : getString(R.string
                .password));
    }

    @OnEditorAction(R.id.passwordEditText) public boolean onSendPassword() {
        if (twoFactor.getVisibility() == View.VISIBLE) {
            twoFactorEditText.requestFocus();
        } else if (endpoint.getVisibility() == View.VISIBLE) {
            endpoint.requestFocus();
        } else {
            doLogin();
        }
        return true;
    }

    @OnEditorAction(R.id.twoFactorEditText) public boolean onSend2FA() {
        doLogin();
        return true;
    }

    @OnEditorAction(R.id.endpointEditText) boolean onSendEndpoint() {
        doLogin();
        return true;
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
        username.setError(isEmpty ? getString(R.string.required_field) : null);
    }

    @Override public void onRequire2Fa() {
        Toasty.warning(App.getInstance(), getString(R.string.two_factors_otp_error)).show();
        twoFactor.setVisibility(View.VISIBLE);
        hideProgress();
    }

    @Override public void onEmptyPassword(boolean isEmpty) {
        password.setError(isEmpty ? getString(R.string.required_field) : null);
    }

    @Override public void onEmptyEndpoint(boolean isEmpty) {
        endpoint.setError(isEmpty ? getString(R.string.required_field) : null);
    }

    @Override public void onSuccessfullyLoggedIn(boolean extraLogin) {
        checkPurchases(() -> {
            hideProgress();
            onRestartApp();
        });
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.LoginTheme);
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getExtras() != null) {
                isBasicAuth = getIntent().getExtras().getBoolean(BundleConstant.YES_NO_EXTRA);
                password.setHint(isBasicAuth ? getString(R.string.password) : getString(R.string.access_token));
                if (getIntent().getExtras().getBoolean(BundleConstant.EXTRA_TWO)) {
                    onOpenBrowser();
                }
            }
        }
        accessTokenCheckbox.setVisibility(isEnterprise() ? View.VISIBLE : View.GONE);
        endpoint.setVisibility(isEnterprise() ? View.VISIBLE : View.GONE);
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
        login.hide();
        AppHelper.hideKeyboard(login);
        AnimHelper.animateVisibility(progress, true);
    }

    @Override public void onBackPressed() {
        startActivity(new Intent(this, LoginChooserActivity.class));
        finish();
    }

    @Override public void hideProgress() {
        progress.setVisibility(View.GONE);
        login.show();
    }

    protected void checkPurchases(@Nullable Action action) {
        getPresenter().manageViewDisposable(RxBillingService.getInstance(this, BuildConfig.DEBUG)
                .getPurchases(ProductType.IN_APP)
                .doOnSubscribe(disposable -> showProgress(0))
                .subscribe((purchases, throwable) -> {
                    hideProgress();
                    if (throwable == null) {
                        Logger.e(purchases);
                        if (purchases != null && !purchases.isEmpty()) {
                            for (Purchase purchase : purchases) {
                                String sku = purchase.sku();
                                if (!InputHelper.isEmpty(sku)) {
                                    DonateActivity.Companion.enableProduct(sku, App.getInstance());
                                }
                            }
                        }
                    } else {
                        throwable.printStackTrace();
                    }
                    if (action != null) action.run();
                }));
    }

    private void doLogin() {
        if (progress.getVisibility() == View.GONE) {
            getPresenter().login(InputHelper.toString(username),
                    InputHelper.toString(password),
                    InputHelper.toString(twoFactor),
                    isBasicAuth, InputHelper.toString(endpoint));
        }
    }
}
