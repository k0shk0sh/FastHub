package com.fastaccess.ui.modules.repos.code.prettifier;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.StateLayout;
import com.prettifier.pretty.PrettifyWebView;

import butterknife.BindView;

/**
 * Created by Kosh on 28 Nov 2016, 9:27 PM
 */

public class ViewerView extends BaseFragment<ViewerMvp.View, ViewerPresenter> implements ViewerMvp.View {

    public static final String TAG = ViewerView.class.getSimpleName();

    @BindView(R.id.webView) PrettifyWebView webView;
    @BindView(R.id.stateLayout) StateLayout stateLayout;

    public static ViewerView newInstance(@NonNull String url) {
        return newInstance(url, false);
    }

    public static ViewerView newInstance(@NonNull String url, boolean isRepo) {
        return newInstance(Bundler.start()
                .put(BundleConstant.ITEM, url)
                .put(BundleConstant.EXTRA, isRepo)
                .end());
    }

    private static ViewerView newInstance(@NonNull Bundle bundle) {
        ViewerView fragmentView = new ViewerView();
        fragmentView.setArguments(bundle);
        return fragmentView;
    }

    @Override public void onSetImageUrl(@NonNull String url) {
        onShowMdProgress();
        webView.loadImage(url);
        webView.setOnContentChangedListener(this);
        webView.setVisibility(View.VISIBLE);
    }

    @Override public void onSetMdText(@NonNull String text, String baseUrl) {
        stateLayout.hideProgress();
        webView.setVisibility(View.VISIBLE);
        webView.setGithubContent(text, baseUrl);
    }

    @Override public void onSetCode(@NonNull String text) {
        stateLayout.hideProgress();
        webView.setVisibility(View.VISIBLE);
        webView.setSource(text);
    }

    @Override public void onShowError(@NonNull String msg) {
        stateLayout.hideProgress();
        showErrorMessage(msg);
    }

    @Override public void onShowError(@StringRes int msg) {
        stateLayout.hideProgress();
        onShowError(getString(msg));
    }

    @Override public void onShowMdProgress() {
        stateLayout.showProgress();
    }

    @Override public void openUrl(@NonNull String url) {
        ActivityHelper.startCustomTab(getActivity(), url);
    }

    @Override public void showProgress(@StringRes int resId) {
        onShowMdProgress();
    }

    @Override public void hideProgress() {
        stateLayout.hideProgress();
    }

    @Override public void showErrorMessage(@NonNull String msgRes) {
        stateLayout.hideProgress();
        super.showErrorMessage(msgRes);
    }

    @Override protected int fragmentLayout() {
        return R.layout.general_viewer_layout;
    }

    @NonNull @Override public ViewerPresenter providePresenter() {
        return new ViewerPresenter();
    }

    @Override public void onContentChanged(int progress) {
        if (progress == 100) {
            if (stateLayout != null) stateLayout.hideProgress();
        }
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (InputHelper.isEmpty(getPresenter().downloadedStream())) {
            getPresenter().onHandleIntent(getArguments());
        } else {
            if (getPresenter().isMarkDown()) {
                onSetMdText(getPresenter().downloadedStream(), getArguments().getString(BundleConstant.EXTRA));
            } else {
                onSetCode(getPresenter().downloadedStream());
            }
        }
    }
}
