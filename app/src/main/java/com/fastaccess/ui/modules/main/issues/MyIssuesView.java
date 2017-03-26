package com.fastaccess.ui.modules.main.issues;

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
import com.fastaccess.ui.adapter.IssuesAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindView;

/**
 * Created by Kosh on 25 Mar 2017, 11:48 PM
 */

public class MyIssuesView extends BaseFragment<MyIssuesMvp.View, MyIssuesPresenter> implements MyIssuesMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    private OnLoadMore<IssueState> onLoadMore;
    private IssuesAdapter adapter;
    private IssueState issueState;
    private RepoPagerMvp.TabsBadgeListener tabsBadgeListener;

    public static MyIssuesView newInstance(@NonNull IssueState issueState) {
        MyIssuesView view = new MyIssuesView();
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

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }

    @NonNull @Override public OnLoadMore<IssueState> getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore<>(getPresenter());
        }
        onLoadMore.setParameter(getIssueState());
        return onLoadMore;
    }

    @Override protected int fragmentLayout() {
        return R.layout.small_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        stateLayout.setEmptyText(R.string.no_issues);
        recycler.setEmptyView(stateLayout, refresh);
        stateLayout.setOnReloadListener(this);
        refresh.setOnRefreshListener(this);
        adapter = new IssuesAdapter(getPresenter().getIssues(), true);
        adapter.setListener(getPresenter());
        getLoadMore().setCurrent_page(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        if (savedInstanceState == null || (getPresenter().getIssues().isEmpty() && !getPresenter().isApiCalled())) {
            onRefresh();
        }
    }

    @NonNull @Override public MyIssuesPresenter providePresenter() {
        return new MyIssuesPresenter();
    }

    @Override public void onSetCount(int totalCount) {
        if (tabsBadgeListener != null) tabsBadgeListener.onSetBadge(getIssueState() == IssueState.open ? 0 : 1, totalCount);
    }

    public IssueState getIssueState() {
        if (issueState == null) {
            issueState = (IssueState) getArguments().getSerializable(BundleConstant.EXTRA);
        }
        return issueState;
    }
}
