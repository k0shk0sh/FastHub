package com.fastaccess.ui.modules.login;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.settings.LanguageBottomSheetDialog;

import net.grandcentrix.thirtyinch.TiPresenter;

import java.util.Arrays;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Action;

/**
 * Created by Kosh on 28 Apr 2017, 9:03 PM
 */

public class LoginChooserActivity extends BaseActivity implements LanguageBottomSheetDialog.LanguageDialogListener {

    @BindView(R.id.language_selector) RelativeLayout language_selector;

    @Override protected int layout() {
        return R.layout.login_chooser_layout;
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

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Arrays.asList(getResources().getStringArray(R.array.languages_array_values)).contains(Locale.getDefault().getLanguage())) {
            String language = PrefHelper.getString("app_language");
            PrefHelper.set("app_language", Locale.getDefault().getLanguage());
            if (!BuildConfig.DEBUG) language_selector.setVisibility(View.GONE);
            if (!Locale.getDefault().getLanguage().equals(language)) recreate();
        }

    }

    @OnClick(R.id.basicAuth) public void onBasicAuthClicked() {
        LoginActivity.start(this, true);
    }

    @OnClick(R.id.accessToken) public void onAccessTokenClicked() {
        LoginActivity.start(this, false);
    }

    @OnClick(R.id.enterprise) void onEnterpriseClicked() {
        if (Login.hasNormalLogin()) LoginActivity.start(this, true, true);
        else showMessage(R.string.error, R.string.enterprise_login_warning);
    }

    @OnClick(R.id.browserLogin) void onOpenBrowser() {
        LoginActivity.startOAuth(this);
    }

    @OnClick(R.id.language_selector_clicker) public void onChangeLanguage() {
        showLanguage();
    }

    @Override public void onLanguageChanged(Action action) {
        try {
            action.run();
            recreate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull @Override public BasePresenter providePresenter() {
        return new BasePresenter();
    }

    private void showLanguage() {
        LanguageBottomSheetDialog languageBottomSheetDialog = new LanguageBottomSheetDialog();
        languageBottomSheetDialog.onAttach((Context) this);
        languageBottomSheetDialog.show(getSupportFragmentManager(), "LanguageBottomSheetDialog");
    }
}
