package com.fastaccess.ui.modules.repos.pull_requests.pull_request;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.PullsIssuesParser;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.PullRequestAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;
import com.fastaccess.ui.modules.repos.extras.popup.IssuePopupFragment;
import com.fastaccess.ui.modules.repos.pull_requests.RepoPullRequestPagerMvp;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.PullRequestPagerActivity;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 03 Dec 2016, 3:56 PM
 */

public class RepoPullRequestFragment extends BaseFragment<RepoPullRequestMvp.View, RepoPullRequestPresenter> implements RepoPullRequestMvp.View {
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    private OnLoadMore<IssueState> onLoadMore;
    private PullRequestAdapter adapter;
    private RepoPullRequestPagerMvp.View pagerCallback;
    private RepoPagerMvp.TabsBadgeListener tabsBadgeListener;

    public static RepoPullRequestFragment newInstance(@NonNull String repoId, @NonNull String login, @NonNull IssueState issueState) {
        RepoPullRequestFragment view = new RepoPullRequestFragment();
        view.setArguments(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, issueState)
                .end());
        return view;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof RepoPullRequestPagerMvp.View) {
            pagerCallback = (RepoPullRequestPagerMvp.View) getParentFragment();
        } else if (context instanceof RepoPullRequestPagerMvp.View) {
            pagerCallback = (RepoPullRequestPagerMvp.View) context;
        }
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

    @Override public void onNotifyAdapter(@Nullable List<PullRequest> items, int page) {
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

    @Override protected int fragmentLayout() {
        return R.layout.micro_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            throw new NullPointerException("Bundle is null, therefore, issues can't be proceeded.");
        }
        stateLayout.setOnReloadListener(this);
        refresh.setOnRefreshListener(this);
        recycler.setEmptyView(stateLayout, refresh);
        adapter = new PullRequestAdapter(getPresenter().getPullRequests(), true);
        adapter.setListener(getPresenter());
        getLoadMore().initialize(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addKeyLineDivider();
        recycler.addOnScrollListener(getLoadMore());
        if (savedInstanceState == null) {
            getPresenter().onFragmentCreated(getArguments());
        } else if (getPresenter().getPullRequests().isEmpty() && !getPresenter().isApiCalled()) {
            onRefresh();
        }
        stateLayout.setEmptyText(R.string.no_pull_requests);
        fastScroller.attachRecyclerView(recycler);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RepoPullRequestMvp.PULL_REQUEST_REQUEST_CODE) {
                boolean isClose = data.getExtras().getBoolean(BundleConstant.EXTRA);
                boolean isOpened = data.getExtras().getBoolean(BundleConstant.EXTRA_TWO);
                if (isClose || isOpened) {
                    onRefresh();
                }
            }
        }
    }

    @NonNull @Override public RepoPullRequestPresenter providePresenter() {
        return new RepoPullRequestPresenter();
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
            onLoadMore = new OnLoadMore<IssueState>(getPresenter()) {
                @Override public void onScrolled(boolean isUp) {
                    super.onScrolled(isUp);
                    if (pagerCallback != null) pagerCallback.onScrolled(isUp);
                }
            };
        }
        onLoadMore.setParameter(getIssueState());
        return onLoadMore;
    }

    @Override public void onUpdateCount(int totalCount) {
        if (tabsBadgeListener != null) tabsBadgeListener.onSetBadge(getPresenter().getIssueState() == IssueState.open ? 0 : 1, totalCount);
    }

    @Override public void onOpenPullRequest(@NonNull PullsIssuesParser parser) {
        Intent intent = PullRequestPagerActivity.createIntent(getContext(), parser.getRepoId(), parser.getLogin(),
                parser.getNumber(), false, isEnterprise());
        startActivityForResult(intent, RepoPullRequestMvp.PULL_REQUEST_REQUEST_CODE);
    }

    @Override public void onShowPullRequestPopup(@NonNull PullRequest item) {
        IssuePopupFragment.showPopup(getChildFragmentManager(), item);
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, getIssueState());
    }

    @Override public void onClick(View view) {
        onRefresh();
    }

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
        if (recycler != null) recycler.scrollToPosition(0);
    }

    private IssueState getIssueState() {
        return ((IssueState) getArguments().getSerializable(BundleConstant.EXTRA_TWO));
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
