package com.fastaccess.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.data.dao.LoginModel;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.login.LoginView;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.dialog.ProgressDialogFragment;

import net.grandcentrix.thirtyinch.TiActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;

/**
 * Created by Kosh on 24 May 2016, 8:48 PM
 */

public abstract class BaseActivity<V extends BaseMvp.FAView, P extends BasePresenter<V>> extends TiActivity<P, V> implements
        BaseMvp.FAView {

    private Toast toast;

    @State boolean isProgressShowing;
    @Nullable @BindView(R.id.toolbar) Toolbar toolbar;
    @Nullable @BindView(R.id.toolbarShadow) View shadowView;

    @LayoutRes protected abstract int layout();

    protected abstract boolean isTransparent();

    protected abstract boolean canBack();

    protected abstract boolean isSecured();

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (layout() != 0) {
            setContentView(layout());
            ButterKnife.bind(this);
        }

        Icepick.setDebug(BuildConfig.DEBUG);
        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }
        setupToolbarAndStatusBar(toolbar);
        if (!isSecured()) {
            if (!isLoggedIn()) {
                startActivity(new Intent(this, LoginView.class));
                finish();
            }
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (canBack()) {
            if (item.getItemId() == android.R.id.home) {
                onBackPressed();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
    }

    @Override public void onDialogDismissed() {

    }//pass

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {

    }//pass

    @Override public void showMessage(@StringRes int titleRes, @StringRes int msgRes) {
        showMessage(getString(titleRes), getString(msgRes));
    }

    @Override public void showMessage(@NonNull String titleRes, @NonNull String msgRes) {
        hideProgress();
        if (!isFinishing()) {
            try {
                getSupportFragmentManager().executePendingTransactions();
                Fragment fragment = AppHelper.getFragmentByTag(getSupportFragmentManager(), "BaseActivity");
                if (fragment == null) {
                    MessageDialogView.newInstance(titleRes, msgRes).show(getSupportFragmentManager(), "BaseActivity");
                } else {
                    MessageDialogView messageDialogView = (MessageDialogView) fragment;
                    messageDialogView.setArguments(MessageDialogView.getBundle(titleRes, msgRes, false, null));
                    messageDialogView.initMessage();
                }
                return;
            } catch (Exception ignored) {}
        }
        if (toast != null) toast.cancel();
        toast = Toast.makeText(this, msgRes, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override public void showErrorMessage(@NonNull String msgRes) {
        showMessage(getString(R.string.error), msgRes);
    }

    @Override public boolean isLoggedIn() {
        return !InputHelper.isEmpty(PrefGetter.getToken()) && LoginModel.getUser() != null;
    }

    @Override public void showProgress(@StringRes int resId) {
        String msg = getString(R.string.in_progress);
        if (resId != 0) {
            msg = getString(resId);
        }
        if (!isProgressShowing) {
            ProgressDialogFragment fragment = (ProgressDialogFragment) AppHelper.getFragmentByTag(getSupportFragmentManager(),
                    ProgressDialogFragment.TAG);
            if (fragment == null) {
                isProgressShowing = true;
                fragment = ProgressDialogFragment.newInstance(msg, false);
                fragment.show(getSupportFragmentManager(), ProgressDialogFragment.TAG);
            }
        }
    }

    @Override public void hideProgress() {
        ProgressDialogFragment fragment = (ProgressDialogFragment) AppHelper.getFragmentByTag(getSupportFragmentManager(),
                ProgressDialogFragment.TAG);
        if (fragment != null) {
            isProgressShowing = false;
            fragment.dismiss();
        }
    }

    private void setupToolbarAndStatusBar(@Nullable Toolbar toolbar) {
        changeStatusBarColor(isTransparent());
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (canBack()) {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        }
    }

    protected void setToolbarIcon(@DrawableRes int res) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(res);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void hideShowShadow(boolean show) {
        if (shadowView != null) shadowView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    protected void changeStatusBarColor(boolean isTransparent) {
        if (!isTransparent) {
            getWindow().setStatusBarColor(ViewHelper.getPrimaryDarkColor(this));
        }
    }
}
