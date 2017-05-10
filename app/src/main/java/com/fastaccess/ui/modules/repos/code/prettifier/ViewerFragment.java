package com.fastaccess.ui.modules.repos.code.prettifier;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.StateLayout;
import com.prettifier.pretty.PrettifyWebView;

import butterknife.BindView;
import icepick.State;

/**
 * Created by Kosh on 28 Nov 2016, 9:27 PM
 */

public class ViewerFragment extends BaseFragment<ViewerMvp.View, ViewerPresenter> implements ViewerMvp.View {

    public static final String TAG = ViewerFragment.class.getSimpleName();

    @BindView(R.id.webView) PrettifyWebView webView;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @State boolean isWrap = PrefGetter.isWrapCode();

    public static ViewerFragment newInstance(@NonNull String url) {
        return newInstance(url, false);
    }

    public static ViewerFragment newInstance(@NonNull String url, boolean isRepo) {
        return newInstance(Bundler.start()
                .put(BundleConstant.ITEM, url)
                .put(BundleConstant.EXTRA, isRepo)
                .end());
    }

    private static ViewerFragment newInstance(@NonNull Bundle bundle) {
        ViewerFragment fragmentView = new ViewerFragment();
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
        Logger.e(!getPresenter().isRepo());
        webView.setGithubContent(text, baseUrl);
    }

    @Override public void onSetCode(@NonNull String text) {
        stateLayout.hideProgress();
        webView.setVisibility(View.VISIBLE);
        webView.setSource(text, isWrap, getPresenter().url());
        getActivity().supportInvalidateOptionsMenu();
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

    @Override public void showMessage(int titleRes, int msgRes) {
        stateLayout.hideProgress();
        super.showMessage(titleRes, msgRes);
    }

    @Override public void showMessage(@NonNull String titleRes, @NonNull String msgRes) {
        stateLayout.hideProgress();
        super.showMessage(titleRes, msgRes);
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

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.wrap_menu_option, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.wrap)
                .setVisible(!getPresenter().isMarkDown() || !getPresenter().isRepo())
                .setChecked(isWrap);
        super.onPrepareOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.wrap) {
            item.setChecked(!item.isChecked());
            isWrap = item.isChecked();
            onSetCode(getPresenter().downloadedStream());
        }
        return super.onOptionsItemSelected(item);
    }
}
