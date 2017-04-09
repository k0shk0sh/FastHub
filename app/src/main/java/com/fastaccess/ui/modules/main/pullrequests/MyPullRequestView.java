package com.fastaccess.ui.modules.main.pullrequests;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.PullRequestAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindView;

/**
 * Created by Kosh on 25 Mar 2017, 11:48 PM
 */

public class MyPullRequestView extends BaseFragment<MyPullRequestsMvp.View, MyPullRequestsPresenter> implements MyPullRequestsMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    private OnLoadMore<IssueState> onLoadMore;
    private PullRequestAdapter adapter;
    private RepoPagerMvp.TabsBadgeListener tabsBadgeListener;

    public static MyPullRequestView newInstance(@NonNull IssueState issueState) {
        MyPullRequestView view = new MyPullRequestView();
        view.setArguments(Bundler.start().put(BundleConstant.EXTRA, issueState).end());
        return view;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof RepoPagerMvp.TabsBadgeListener) {
            tabsBadgeListener = (RepoPagerMvp.TabsBadgeListener) getParentFragment();
        } else if (context instanceof RepoPagerMvp.TabsBadgeListener) {
            tabsBadgeListener = (RepoPagerMvp.TabsBadgeListener) context;
        }
    }

    @Override public void onDetach() {
        tabsBadgeListener = null;
        super.onDetach();
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, getIssueState());
    }

    @Override public void onClick(View view) {
        onRefresh();
    }

    @Override public void onNotifyAdapter() {
        hideProgress();
        adapter.notifyDataSetChanged();
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

    @NonNull @Override public OnLoadMore<IssueState> getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore<>(getPresenter());
        }
        onLoadMore.setParameter(getIssueState());
        return onLoadMore;
    }

    @Override public void onSetCount(int totalCount) {
        if (tabsBadgeListener != null) tabsBadgeListener.onSetBadge(getIssueState() == IssueState.open ? 0 : 1, totalCount);
    }

    @Override protected int fragmentLayout() {
        return R.layout.small_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recycler.setEmptyView(stateLayout, refresh);
        stateLayout.setOnReloadListener(this);
        refresh.setOnRefreshListener(this);
        adapter = new PullRequestAdapter(getPresenter().getPullRequests(), false, true);
        adapter.setListener(getPresenter());
        recycler.addDivider();
        getLoadMore().setCurrent_page(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        if (savedInstanceState == null || (getPresenter().getPullRequests().isEmpty() && !getPresenter().isApiCalled())) {
            onRefresh();
        }
        stateLayout.setEmptyText(getIssueState() == IssueState.open ? R.string.no_open_pull_requests : R.string.no_closed_pull_request);
    }

    @NonNull @Override public MyPullRequestsPresenter providePresenter() {
        return new MyPullRequestsPresenter();
    }

    public IssueState getIssueState() {
        return (IssueState) getArguments().getSerializable(BundleConstant.EXTRA);
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
