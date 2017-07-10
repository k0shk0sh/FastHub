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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.evernote.android.state.State;
import com.fastaccess.App;
import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.main.MainActivity;
import com.fastaccess.ui.modules.settings.LanguageBottomSheetDialog;
import com.fastaccess.ui.widgets.FontCheckbox;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.miguelbcr.io.rx_billing_service.RxBillingService;
import com.miguelbcr.io.rx_billing_service.entities.ProductType;
import com.miguelbcr.io.rx_billing_service.entities.Purchase;

import java.util.Arrays;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.Optional;
import es.dmoral.toasty.Toasty;
import io.reactivex.functions.Action;

/**
 * Created by Kosh on 08 Feb 2017, 9:10 PM
 */

public class LoginActivity extends BaseActivity<LoginMvp.View, LoginPresenter> implements LoginMvp.View,
        LanguageBottomSheetDialog.LanguageDialogListener {

    @Nullable @BindView(R.id.language_selector) RelativeLayout language_selector;
    @Nullable @BindView(R.id.usernameEditText) TextInputEditText usernameEditText;
    @Nullable @BindView(R.id.username) TextInputLayout username;
    @Nullable @BindView(R.id.passwordEditText) TextInputEditText passwordEditText;
    @Nullable @BindView(R.id.password) TextInputLayout password;
    @Nullable @BindView(R.id.twoFactor) TextInputLayout twoFactor;
    @Nullable @BindView(R.id.twoFactorEditText) TextInputEditText twoFactorEditText;
    @Nullable @BindView(R.id.login) FloatingActionButton login;
    @Nullable @BindView(R.id.progress) ProgressBar progress;
    @Nullable @BindView(R.id.accessTokenCheckbox) FontCheckbox accessTokenCheckbox;
    @Nullable @BindView(R.id.endpoint) TextInputLayout endpoint;
    @State boolean isBasicAuth;
    @State boolean isEnterprise;
    @State boolean extraLogin;

    public static void start(@NonNull Activity activity, boolean isBasicAuth) {
        PrefGetter.setEnterpriseUrl(null);
        start(activity, isBasicAuth, false);
    }

    public static void start(@NonNull Activity activity, boolean isBasicAuth, boolean isEnterprise) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.YES_NO_EXTRA, isBasicAuth)
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .end());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("smartLock", true);
        activity.startActivity(intent);
        activity.finish();
    }

    @Optional @OnClick(R.id.browserLogin) void onOpenBrowser() {
        if (isEnterprise && InputHelper.isEmpty(endpoint)) {
            endpoint.setError(getString(R.string.required_field));
            return;
        }
        if (endpoint != null) endpoint.setError(null);
        Uri uri = getPresenter().getAuthorizationUrl(endpoint != null ? InputHelper.toString(endpoint) : null);
        ActivityHelper.startCustomTab(this, uri);
    }

    @Optional @OnClick(R.id.login) public void onClick() {
        doLogin();
    }

    @Optional @OnCheckedChanged(R.id.accessTokenCheckbox) void onCheckChanged(boolean checked) {
        isBasicAuth = !checked;
        if (password != null) {
            password.setHint(checked ? getString(R.string.access_token) : getString(R.string.password));
        }
    }

    @Optional @OnEditorAction(R.id.passwordEditText) public boolean onSendPassword() {
        if (twoFactor == null || twoFactorEditText == null) return false;
        if (twoFactor.getVisibility() == View.VISIBLE) {
            twoFactorEditText.requestFocus();
        } else if (endpoint != null && endpoint.getVisibility() == View.VISIBLE) {
            endpoint.requestFocus();
        } else {
            doLogin();
        }
        return true;
    }

    @Optional @OnEditorAction(R.id.twoFactorEditText) public boolean onSend2FA() {
        doLogin();
        return true;
    }

    @Optional @OnEditorAction(R.id.endpointEditText) boolean onSendEndpoint() {
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
        Toasty.warning(App.getInstance(), getString(R.string.two_factors_otp_error)).show();
        if (twoFactor == null) return;
        twoFactor.setVisibility(View.VISIBLE);
        hideProgress();
    }

    @Override public void onEmptyPassword(boolean isEmpty) {
        if (password == null) return;
        password.setError(isEmpty ? getString(R.string.required_field) : null);
    }

    @Override public void onEmptyEndpoint(boolean isEmpty) {
        if (endpoint != null) endpoint.setError(isEmpty ? getString(R.string.required_field) : null);
    }

    @Override public void onSuccessfullyLoggedIn(boolean extraLogin) {
        if (isEnterprise && extraLogin && !Login.hasNormalLogin()) {
            MessageDialogView.newInstance(getString(R.string.details), getString(R.string.enterprise_login_warning), false, true)
                    .show(getSupportFragmentManager(), MessageDialogView.TAG);
        } else {
            checkPurchases(() -> {
                hideProgress();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.LoginTheme);
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getExtras() != null) {
                isBasicAuth = getIntent().getExtras().getBoolean(BundleConstant.YES_NO_EXTRA);
                isEnterprise = getIntent().getExtras().getBoolean(BundleConstant.IS_ENTERPRISE);
            }
        }
        if (endpoint != null && accessTokenCheckbox != null) {
            accessTokenCheckbox.setVisibility(isEnterprise ? View.VISIBLE : View.GONE);
            endpoint.setVisibility(isEnterprise ? View.VISIBLE : View.GONE);
        }
        if (Arrays.asList(getResources().getStringArray(R.array.languages_array_values)).contains(Locale.getDefault().getLanguage())) {
            String language = PrefHelper.getString("app_language");
            PrefHelper.set("app_language", Locale.getDefault().getLanguage());
            if (!BuildConfig.DEBUG) if (language_selector != null) language_selector.setVisibility(View.GONE);
            if (!Locale.getDefault().getLanguage().equals(language)) recreate();
        }

    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getPresenter().onHandleAuthIntent(intent, extraLogin);
        setIntent(null);
    }

    @Override protected void onResume() {
        super.onResume();
        getPresenter().onHandleAuthIntent(getIntent(), extraLogin);
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
        AppHelper.hideKeyboard(login);
        AnimHelper.animateVisibility(progress, true);
    }

    @Override public void onBackPressed() {
        if (!(this instanceof LoginChooserActivity)) {
            startActivity(new Intent(this, LoginChooserActivity.class));
            finish();
        } else {
            finish();
        }
    }

    @Override public void hideProgress() {
        if (login == null || progress == null) return;
        progress.setVisibility(View.GONE);
        login.show();
    }

    @Override public void onLanguageChanged(Action action) {
        try {
            action.run();
            recreate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (isOk) {
            getUserToken();
        }
    }

    @Override public void onDialogDismissed() {
        super.onDialogDismissed();
        getUserToken();
    }

    private void getUserToken() {
        extraLogin = true;
        Uri uri = getPresenter().getAuthorizationUrl(null);
        ActivityHelper.startCustomTab(this, uri);
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
                                if (sku != null) {
                                    if (sku.equalsIgnoreCase(getString(R.string.donation_product_1))) {
                                        PrefGetter.enableAmlodTheme();
                                    } else {
                                        PrefGetter.setProItems();
                                    }
                                }
                            }
                        }
                    } else {
                        throwable.printStackTrace();
                    }
                    if (action != null) action.run();
                }));
    }

    private void showLanguage() {
        LanguageBottomSheetDialog languageBottomSheetDialog = new LanguageBottomSheetDialog();
        languageBottomSheetDialog.onAttach((Context) this);
        languageBottomSheetDialog.show(getSupportFragmentManager(), "LanguageBottomSheetDialog");
    }

    private void doLogin() {
        if (progress == null || twoFactor == null || username == null || password == null) return;
        if (progress.getVisibility() == View.GONE) {
            getPresenter().login(InputHelper.toString(username),
                    InputHelper.toString(password),
                    InputHelper.toString(twoFactor),
                    isBasicAuth, endpoint != null ? InputHelper.toString(endpoint) : null, isEnterprise);
        }
    }
}
