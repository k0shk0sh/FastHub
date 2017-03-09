package com.fastaccess.ui.modules.profile.starred;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.ReposAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindView;

/**
 * Created by Kosh on 03 Dec 2016, 3:56 PM
 */

public class ProfileStarredView extends BaseFragment<ProfileStarredMvp.View, ProfileStarredPresenter> implements ProfileStarredMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    private OnLoadMore<String> onLoadMore;
    private ReposAdapter adapter;

    public static ProfileStarredView newInstance(@NonNull String username) {
        ProfileStarredView view = new ProfileStarredView();
        view.setArguments(Bundler.start().put(BundleConstant.EXTRA, username).end());
        return view;
    }

    @Override public void onNotifyAdapter() {

        hideProgress();
        adapter.notifyDataSetChanged();
    }

    @Override protected int fragmentLayout() {
        return R.layout.small_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            throw new NullPointerException("Bundle is null, username is required");
        }
        stateLayout.setOnReloadListener(this);
        refresh.setOnRefreshListener(this);
        recycler.setEmptyView(stateLayout, refresh);
        getLoadMore().setCurrent_page(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        adapter = new ReposAdapter(getPresenter().getRepos(), true);
        adapter.setListener(getPresenter());
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        recycler.addDivider();
        if (getPresenter().getRepos().isEmpty() && !getPresenter().isApiCalled()) {
            onRefresh();
        }
    }

    @NonNull @Override public ProfileStarredPresenter providePresenter() {
        return new ProfileStarredPresenter();
    }


    @Override public void showProgress(@StringRes int resId) {

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


    @NonNull @Override public OnLoadMore<String> getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore<>(getPresenter(), getArguments().getString(BundleConstant.EXTRA));
        }
        return onLoadMore;
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, getArguments().getString(BundleConstant.EXTRA));
    }

    @Override public void onClick(View view) {
        onRefresh();
    }
}
