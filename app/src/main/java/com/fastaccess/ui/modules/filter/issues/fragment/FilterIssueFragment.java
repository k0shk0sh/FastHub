package com.fastaccess.ui.modules.filter.issues.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.adapter.IssuesAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.filter.issues.FilterIssuesActivityMvp;
import com.fastaccess.ui.modules.repos.extras.popup.IssuePopupFragment;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 09 Apr 2017, 7:13 PM
 */

public class FilterIssueFragment extends BaseFragment<FilterIssuesMvp.View, FilterIssuePresenter> implements FilterIssuesMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    private OnLoadMore<String> onLoadMore;
    private IssuesAdapter adapter;

    @State IssueState issueState = IssueState.open;
    @State boolean isIssue;
    @State String query;

    private FilterIssuesActivityMvp.View callback;

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        callback = (FilterIssuesActivityMvp.View) context;
    }

    @Override public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override public void onRefresh() {
        if (!InputHelper.isEmpty(query)) {
            getPresenter().onCallApi(1, query);
        }
    }

    @Override public void onClick(View v) {
        onRefresh();
    }

    @Override public void onClear() {
        hideProgress();
        getPresenter().getIssues().clear();
        adapter.notifyDataSetChanged();
    }

    @Override public void onSearch(@NonNull String query, boolean isOpen, boolean isIssue, boolean isEnterprise) {
        getPresenter().setEnterprise(isEnterprise);
        this.query = query;
        this.issueState = isOpen ? IssueState.open : IssueState.closed;
        this.isIssue = isIssue;
        onClear();
        onRefresh();
    }

    @Override public void onNotifyAdapter(@Nullable List<Issue> items, int page) {
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

    @NonNull @Override public OnLoadMore<String> getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore<>(getPresenter());
        }
        onLoadMore.setParameter(query);
        return onLoadMore;
    }

    @Override public void onSetCount(int totalCount) {
        if (callback != null) {
            callback.onSetCount(totalCount, issueState == IssueState.open);
        }
    }

    @Override public void onItemClicked(@NonNull Issue item) {
        SchemeParser.launchUri(getContext(), item.getHtmlUrl());
    }

    @Override protected int fragmentLayout() {
        return R.layout.micro_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        stateLayout.setEmptyText(R.string.no_search_results);
        recycler.setEmptyView(stateLayout, refresh);
        stateLayout.setOnReloadListener(this);
        refresh.setOnRefreshListener(this);
        adapter = new IssuesAdapter(getPresenter().getIssues(), true, false, true);
        adapter.setListener(getPresenter());
        getLoadMore().initialize(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        recycler.addKeyLineDivider();
        if (savedInstanceState != null) {
            if (!InputHelper.isEmpty(query) && getPresenter().getIssues().isEmpty()) {
                onRefresh();
            }
        }
        fastScroller.attachRecyclerView(recycler);
    }

    @NonNull @Override public FilterIssuePresenter providePresenter() {
        return new FilterIssuePresenter();
    }

    @Override public void onShowPopupDetails(@NonNull Issue item) {
        IssuePopupFragment.showPopup(getChildFragmentManager(), item);
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
