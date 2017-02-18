package com.fastaccess.ui.modules.profile.gists;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.Logger;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.GistsAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

public class ProfileGistsView extends BaseFragment<ProfileGistsMvp.View, ProfileGistsPresenter> implements ProfileGistsMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;

    private GistsAdapter adapter;
    private OnLoadMore<String> onLoadMore;

    public static ProfileGistsView newInstance(@NonNull String login) {
        ProfileGistsView view = new ProfileGistsView();
        view.setArguments(Bundler.start().put(BundleConstant.EXTRA, login).end());
        return view;
    }

    @Override protected int fragmentLayout() {
        return R.layout.small_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments().getString(BundleConstant.EXTRA) == null) {
            throw new NullPointerException("Username is null");
        }
        refresh.setOnRefreshListener(this);
        stateLayout.setOnReloadListener(this);
        recycler.setEmptyView(stateLayout, refresh);
        adapter = new GistsAdapter(getPresenter().getGists(), true);
        adapter.setListener(getPresenter());
        getLoadMore().setCurrent_page(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        if (getPresenter().getGists().isEmpty() && !getPresenter().isApiCalled()) {
            onRefresh();
        }
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, getArguments().getString(BundleConstant.EXTRA));
    }

    @Override public void onNotifyAdapter() {

        hideProgress();
        adapter.notifyDataSetChanged();
    }

    @Override public void showProgress(@StringRes int resId) {
        refresh.setRefreshing(true);
        stateLayout.showProgress();
    }

    @Override public void hideProgress() {
        refresh.setRefreshing(false);
        stateLayout.hideProgress();
    }

    @Override public void showErrorMessage(@NonNull String msgRes) {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
        super.showErrorMessage(msgRes);
    }

    @NonNull @Override public ProfileGistsPresenter providePresenter() {
        return new ProfileGistsPresenter();
    }

    @NonNull @Override public OnLoadMore<String> getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore<>(getPresenter(), getArguments().getString(BundleConstant.EXTRA));
        }
        return onLoadMore;
    }

    @Override public void onDestroyView() {
        recycler.removeOnScrollListener(getLoadMore());
        super.onDestroyView();
    }

    @Override public void onClick(View view) {
        onRefresh();
    }
}
