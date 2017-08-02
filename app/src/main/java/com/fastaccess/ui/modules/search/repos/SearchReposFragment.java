package com.fastaccess.ui.modules.search.repos;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.ReposAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.search.SearchMvp;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 03 Dec 2016, 3:56 PM
 */

public class SearchReposFragment extends BaseFragment<SearchReposMvp.View, SearchReposPresenter> implements SearchReposMvp.View {

    @State String searchQuery = "";
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    private OnLoadMore<String> onLoadMore;
    private ReposAdapter adapter;
    private SearchMvp.View countCallback;

    public static SearchReposFragment newInstance() {
        return new SearchReposFragment();
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchMvp.View) {
            countCallback = (SearchMvp.View) context;
        }
    }

    @Override public void onDetach() {
        countCallback = null;
        super.onDetach();
    }

    @Override public void onNotifyAdapter(@Nullable List<Repo> items, int page) {
        hideProgress();
        if (items == null || items.isEmpty()) {
            adapter.clear();
            return;
        }
        if (page <= 1) {
            adapter.insertItems(items);
        } else {
            adapter.addItems(items);
        }
    }

    @Override public void onSetTabCount(int count) {
        if (countCallback != null) countCallback.onSetCount(count, 0);
    }

    @Override protected int fragmentLayout() {
        return R.layout.micro_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            stateLayout.hideProgress();
        }
        stateLayout.setEmptyText(R.string.no_search_results);
        getLoadMore().initialize(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
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
        fastScroller.attachRecyclerView(recycler);
    }

    @NonNull @Override public SearchReposPresenter providePresenter() {
        return new SearchReposPresenter();
    }

    @Override public void hideProgress() {
        refresh.setRefreshing(false);
        stateLayout.hideProgress();
    }

    @Override public void showProgress(@StringRes int resId) {
        refresh.setRefreshing(true);
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

    @Override public void onSetSearchQuery(@NonNull String query) {
        this.searchQuery = query;
        getLoadMore().reset();
        adapter.clear();
        if (!InputHelper.isEmpty(query)) {
            recycler.removeOnScrollListener(getLoadMore());
            recycler.addOnScrollListener(getLoadMore());
            onRefresh();
        }
    }

    @Override public void onQueueSearch(@NonNull String query) {
        this.searchQuery = query;
        if (getView() != null)
            onSetSearchQuery(query);
    }

    @NonNull @Override public OnLoadMore<String> getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore<>(getPresenter(), searchQuery);
        }
        onLoadMore.setParameter(searchQuery);
        return onLoadMore;
    }

    @Override public void onRefresh() {
        if (searchQuery.length() == 0) {
            refresh.setRefreshing(false);
            return;
        }
        getPresenter().onCallApi(1, searchQuery);
    }

    @Override public void onClick(View view) {
        onRefresh();
    }

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
        if (recycler != null) recycler.scrollToPosition(0);
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
