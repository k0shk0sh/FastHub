package com.fastaccess.ui.modules.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.widget.ProgressBar;

import com.fastaccess.R;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.main.MainView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 08 Feb 2017, 9:10 PM
 */

public class LoginView extends BaseActivity<LoginMvp.View, LoginPresenter> implements LoginMvp.View {


    @BindView(R.id.usernameEditText) TextInputEditText usernameEditText;
    @BindView(R.id.username) TextInputLayout username;
    @BindView(R.id.passwordEditText) TextInputEditText passwordEditText;
    @BindView(R.id.password) TextInputLayout password;
    @BindView(R.id.login) FloatingActionButton login;
    @BindView(R.id.progress) ProgressBar progress;

    @Override protected int layout() {
        return R.layout.login_layout;
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

    @Override public void onEmptyPassword(boolean isEmpty) {
        password.setError(isEmpty ? getString(R.string.required_field) : null);
    }

    @Override public void onSuccessfullyLoggedIn() {
        hideProgress();
        startActivity(new Intent(this, MainView.class));
        finish();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick(R.id.login) public void onClick() {
        getPresenter().login(InputHelper.toString(username), InputHelper.toString(password));
    }

    @Override public void showErrorMessage(@NonNull String msgRes) {
        hideProgress();
        super.showErrorMessage(msgRes);
    }

    @Override public void showMessage(@StringRes int titleRes, @StringRes int msgRes) {
        hideProgress();
        super.showMessage(titleRes, msgRes);
    }

    @Override public void showProgress(@StringRes int resId) {
        AnimHelper.animateVisibility(login, false, new AnimHelper.AnimationCallback() {
            @Override public void onAnimationEnd() {
                AnimHelper.animateVisibility(progress, true);
            }

            @Override public void onAnimationStart() {}
        });
    }

    @Override public void hideProgress() {
        AnimHelper.animateVisibility(progress, false, new AnimHelper.AnimationCallback() {
            @Override public void onAnimationEnd() {
                AnimHelper.animateVisibility(login, true);
            }

            @Override public void onAnimationStart() {}
        });
    }
}
