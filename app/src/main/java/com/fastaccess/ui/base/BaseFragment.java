package com.fastaccess.ui.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evernote.android.state.StateSaver;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import net.grandcentrix.thirtyinch.TiFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Kosh on 27 May 2016, 7:54 PM
 */

public abstract class BaseFragment<V extends BaseMvp.FAView, P extends BasePresenter<V>> extends TiFragment<P, V> implements BaseMvp.FAView {

    protected BaseMvp.FAView callback;

    @Nullable private Unbinder unbinder;

    @LayoutRes protected abstract int fragmentLayout();

    protected abstract void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState);

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseMvp.FAView) {
            callback = (BaseMvp.FAView) context;
        }
    }

    @Override public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        StateSaver.saveInstanceState(this, outState);
        getPresenter().onSaveInstanceState(outState);
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            StateSaver.restoreInstanceState(this, savedInstanceState);
            getPresenter().onRestoreInstanceState(savedInstanceState);
        }
        getPresenter().setEnterprise(isEnterprise());
    }

    @SuppressLint("RestrictedApi") @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentLayout() != 0) {
            final Context contextThemeWrapper = new ContextThemeWrapper(getContext(), getContext().getTheme());
            LayoutInflater themeAwareInflater = inflater.cloneInContext(contextThemeWrapper);
            View view = themeAwareInflater.inflate(fragmentLayout(), container, false);
            unbinder = ButterKnife.bind(this, view);
            return view;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Login.getUser() != null) {
            onFragmentCreated(view, savedInstanceState);
        }
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
    }

    @Override public void showProgress(@StringRes int resId) {
        callback.showProgress(resId);
    }

    @Override public void showBlockingProgress(int resId) {
        callback.showBlockingProgress(resId);
    }

    @Override public void hideProgress() {
        if (callback != null) callback.hideProgress();
    }

    @Override public void showMessage(@StringRes int titleRes, @StringRes int msgRes) {
        callback.showMessage(titleRes, msgRes);
    }

    @Override public void showMessage(@NonNull String titleRes, @NonNull String msgRes) {
        callback.showMessage(titleRes, msgRes);
    }

    @Override public void showErrorMessage(@NonNull String msgRes) {
        callback.showErrorMessage(msgRes);
    }

    @Override public boolean isLoggedIn() {
        return callback.isLoggedIn();
    }

    @Override public void onRequireLogin() {
        callback.onRequireLogin();
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {}

    @Override public void onDialogDismissed() {}

    @Override public void onLogoutPressed() {
        callback.onLogoutPressed();
    }

    @Override public void onThemeChanged() {
        callback.onThemeChanged();
    }

    @Override public void onOpenSettings() {
        callback.onOpenSettings();
    }

    @Override public void onScrollTop(int index) {}

    @Override public boolean isEnterprise() {
        return callback != null && callback.isEnterprise();
    }

    @Override public void onOpenUrlInBrowser() {
        callback.onOpenUrlInBrowser();
    }

    protected boolean isSafe() {
        return getView() != null && getActivity() != null && !getActivity().isFinishing();
    }
}
