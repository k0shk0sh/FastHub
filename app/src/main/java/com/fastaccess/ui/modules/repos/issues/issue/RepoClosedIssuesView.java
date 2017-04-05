package com.fastaccess.ui.modules.repos.issues.issue;

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
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.IssuesAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;
import com.fastaccess.ui.modules.repos.issues.RepoIssuesPagerMvp;
import com.fastaccess.ui.modules.repos.issues.issue.details.IssuePagerView;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindView;

/**
 * Created by Kosh on 03 Dec 2016, 3:56 PM
 */

public class RepoClosedIssuesView extends BaseFragment<RepoIssuesMvp.View, RepoIssuesPresenter> implements RepoIssuesMvp.View {
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    private OnLoadMore<IssueState> onLoadMore;
    private IssuesAdapter adapter;
    private RepoPagerMvp.TabsBadgeListener tabsBadgeListener;
    private RepoIssuesPagerMvp.View pagerCallback;

    public static RepoClosedIssuesView newInstance(@NonNull String repoId, @NonNull String login) {
        RepoClosedIssuesView view = new RepoClosedIssuesView();
        view.setArguments(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .end());
        return view;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof RepoIssuesPagerMvp.View) {
            pagerCallback = (RepoIssuesPagerMvp.View) getParentFragment();
        } else if (context instanceof RepoIssuesPagerMvp.View) {
            pagerCallback = (RepoIssuesPagerMvp.View) context;
        }
        if (getParentFragment() instanceof RepoPagerMvp.TabsBadgeListener) {
            tabsBadgeListener = (RepoPagerMvp.TabsBadgeListener) getParentFragment();
        } else if (context instanceof RepoPagerMvp.TabsBadgeListener) {
            tabsBadgeListener = (RepoPagerMvp.TabsBadgeListener) context;
        }
    }

    @Override public void onDetach() {
        pagerCallback = null;
        tabsBadgeListener = null;
        super.onDetach();
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
            throw new NullPointerException("Bundle is null, therefore, issues can't be proceeded.");
        }
        stateLayout.setEmptyText(R.string.no_closed_issues);
        stateLayout.setOnReloadListener(this);
        refresh.setOnRefreshListener(this);
        recycler.setEmptyView(stateLayout, refresh);
        adapter = new IssuesAdapter(getPresenter().getIssues(), true);
        adapter.setListener(getPresenter());
        getLoadMore().setCurrent_page(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        if (savedInstanceState == null) {
            getPresenter().onFragmentCreated(getArguments(), IssueState.closed);
        } else if (getPresenter().getIssues().isEmpty() && !getPresenter().isApiCalled()) {
            onRefresh();
        }
    }

    @NonNull @Override public RepoIssuesPresenter providePresenter() {
        return new RepoIssuesPresenter();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RepoIssuesMvp.ISSUE_REQUEST_CODE && data != null) {
                boolean isClose = data.getExtras().getBoolean(BundleConstant.EXTRA);
                boolean isOpened = data.getExtras().getBoolean(BundleConstant.EXTRA_TWO);
                if (isClose) {
                    onRefresh();
                } else if (isOpened) {
                    if (pagerCallback != null) pagerCallback.setCurrentItem(0, true);
                    onRefresh();
                } //else ignore!
            }
        }
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
        onLoadMore.setParameter(IssueState.closed);
        return onLoadMore;
    }

    @Override public void onAddIssue() {
        //DO NOTHING
    }

    @Override public void onUpdateCount(int totalCount) {
        if (tabsBadgeListener != null) tabsBadgeListener.onSetBadge(1, totalCount);
    }

    @Override public void onOpenIssue(@NonNull PullsIssuesParser parser) {
        startActivityForResult(IssuePagerView.createIntent(getContext(), parser.getRepoId(), parser.getLogin(),
                parser.getNumber()), RepoIssuesMvp.ISSUE_REQUEST_CODE);
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, IssueState.closed);
    }

    @Override public void onClick(View view) {
        onRefresh();
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
