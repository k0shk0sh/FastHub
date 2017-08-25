package com.fastaccess.ui.modules.main.issues;

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
import com.fastaccess.data.dao.types.MyIssuesType;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.IssuesAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;
import com.fastaccess.ui.modules.repos.extras.popup.IssuePopupFragment;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 25 Mar 2017, 11:48 PM
 */

public class MyIssuesFragment extends BaseFragment<MyIssuesMvp.View, MyIssuesPresenter> implements MyIssuesMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    @State IssueState issueState;
    private OnLoadMore<IssueState> onLoadMore;
    private IssuesAdapter adapter;
    private MyIssuesType issuesType;
    private RepoPagerMvp.TabsBadgeListener tabsBadgeListener;

    public static MyIssuesFragment newInstance(@NonNull IssueState issueState, @NonNull MyIssuesType issuesType) {
        MyIssuesFragment view = new MyIssuesFragment();
        view.setArguments(Bundler.start()
                .put(BundleConstant.EXTRA, issueState)
                .put(BundleConstant.EXTRA_TWO, issuesType)
                .end());
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
        getPresenter().onCallApi(1, issueState);
    }

    @Override public void onClick(View view) {
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

    @NonNull @Override public OnLoadMore<IssueState> getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore<>(getPresenter());
        }
        onLoadMore.setParameter(issueState);
        return onLoadMore;
    }

    @Override protected int fragmentLayout() {
        return R.layout.micro_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            issueState = (IssueState) getArguments().getSerializable(BundleConstant.EXTRA);
        }
        getPresenter().onSetIssueType(getIssuesType());
        stateLayout.setEmptyText(R.string.no_issues);
        recycler.setEmptyView(stateLayout, refresh);
        stateLayout.setOnReloadListener(this);
        refresh.setOnRefreshListener(this);
        adapter = new IssuesAdapter(getPresenter().getIssues(), false, true);
        adapter.setListener(getPresenter());
        getLoadMore().initialize(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addDivider();
        recycler.addOnScrollListener(getLoadMore());
        if (savedInstanceState == null || (getPresenter().getIssues().isEmpty() && !getPresenter().isApiCalled())) {
            onRefresh();
        }
        fastScroller.attachRecyclerView(recycler);
    }

    @NonNull @Override public MyIssuesPresenter providePresenter() {
        return new MyIssuesPresenter();
    }

    @Override public void onSetCount(int totalCount) {
        if (tabsBadgeListener != null) {
            switch (getIssuesType()) {
                case CREATED:
                    tabsBadgeListener.onSetBadge(0, totalCount);
                    break;
                case ASSIGNED:
                    tabsBadgeListener.onSetBadge(1, totalCount);
                    break;
                case MENTIONED:
                    tabsBadgeListener.onSetBadge(2, totalCount);
                    break;
                case PARTICIPATED:
                    tabsBadgeListener.onSetBadge(3, totalCount);
                    break;
            }
        }
    }

    @Override public void onFilterIssue(@NonNull IssueState issueState) {
        if (this.issueState != null && this.issueState != issueState) {
            this.issueState = issueState;
            getArguments().putSerializable(BundleConstant.ITEM, issueState);
            getLoadMore().reset();
            adapter.clear();
            onRefresh();
        }
    }

    @Override public void onShowPopupDetails(@NonNull Issue item) {
        IssuePopupFragment.showPopup(getChildFragmentManager(), item);
    }

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
        if (recycler != null) recycler.scrollToPosition(0);
    }

    private MyIssuesType getIssuesType() {
        if (issuesType == null) {
            issuesType = (MyIssuesType) getArguments().getSerializable(BundleConstant.EXTRA_TWO);
        }
        return issuesType;
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
