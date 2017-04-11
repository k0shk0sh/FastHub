package com.fastaccess.ui.modules.search.repos;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.ReposAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindView;
import icepick.State;

/**
 * Created by Kosh on 03 Dec 2016, 3:56 PM
 */

public class SearchReposView extends BaseFragment<SearchReposMvp.View, SearchReposPresenter> implements SearchReposMvp.View {

    @State String searchQuery;
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    private OnLoadMore<String> onLoadMore;
    private ReposAdapter adapter;

    public static SearchReposView newInstance() {
        return new SearchReposView();
    }

    @Override public void onNotifyAdapter() {
        hideProgress();
        adapter.notifyDataSetChanged();
    }

    @Override protected int fragmentLayout() {
        return R.layout.small_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            stateLayout.hideProgress();
        }
        stateLayout.setEmptyText(R.string.no_search_results);
        getLoadMore().setCurrent_page(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        stateLayout.setOnReloadListener(this);
        refresh.setOnRefreshListener(this);
        recycler.setEmptyView(stateLayout, refresh);
        adapter = new ReposAdapter(getPresenter().getRepos(), true, true);
        adapter.setListener(getPresenter());
        recycler.setAdapter(adapter);
        recycler.addKeyLineDivider();
        if (!InputHelper.isEmpty(searchQuery) && getPresenter().getRepos().isEmpty() && !getPresenter().isApiCalled()) {
            onRefresh();
        }
        if (InputHelper.isEmpty(searchQuery)) {
            stateLayout.showEmptyState();
        }
    }

    @NonNull @Override public SearchReposPresenter providePresenter() {
        return new SearchReposPresenter();
    }

    @Override public void hideProgress() {
        refresh.setRefreshing(false);
        stateLayout.hideProgress();
    }

    @Override public void showProgress(@StringRes int resId) {

        stateLayout.showProgress();
    }

    @Override public void showErrorMessage(@NonNull String message) {
        showReload();
        super.showErrorMessage(message);
    }

    @Override public void showMessage(int titleRes, int msgRes) {
        showReload();
        super.showMessage(titleRes, msgRes);
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }

    @Override public void onSetSearchQuery(@NonNull String query) {
        this.searchQuery = query;
        getLoadMore().reset();
        getPresenter().getRepos().clear();
        onNotifyAdapter();
        recycler.scrollToPosition(0);
        if (!InputHelper.isEmpty(query)) {
            recycler.removeOnScrollListener(getLoadMore());
            recycler.addOnScrollListener(getLoadMore());
            onRefresh();
        }
    }

    @NonNull @Override public OnLoadMore<String> getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore<>(getPresenter(), searchQuery);
        }
        onLoadMore.setParameter(searchQuery);
        return onLoadMore;
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, searchQuery);
    }

    @Override public void onClick(View view) {
        onRefresh();
    }
}
