package com.fastaccess.ui.modules.repos.pull_requests.pull_request;

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
 * Created by Kosh on 03 Dec 2016, 3:56 PM
 */

public class RepoPullRequestView extends BaseFragment<RepoPullRequestMvp.View, RepoPullRequestPresenter> implements RepoPullRequestMvp.View {
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    private OnLoadMore<IssueState> onLoadMore;
    private PullRequestAdapter adapter;
    private RepoPagerMvp.TabsBadgeListener tabsBadgeListener;

    public static RepoPullRequestView newInstance(@NonNull String repoId, @NonNull String login, @NonNull IssueState issueState) {
        RepoPullRequestView view = new RepoPullRequestView();
        view.setArguments(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, issueState)
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
        stateLayout.setOnReloadListener(this);
        refresh.setOnRefreshListener(this);
        recycler.setEmptyView(stateLayout, refresh);
        adapter = new PullRequestAdapter(getPresenter().getPullRequests(), true);
        adapter.setListener(getPresenter());
        getLoadMore().setCurrent_page(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        if (savedInstanceState == null) {
            getPresenter().onFragmentCreated(getArguments());
        } else if (getPresenter().getPullRequests().isEmpty() && !getPresenter().isApiCalled()) {
            onRefresh();
        }
        stateLayout.setEmptyText(getPresenter().getIssueState() == IssueState.open
                                 ? R.string.no_open_pull_requests : R.string.no_closed_pull_request);
    }

    @NonNull @Override public RepoPullRequestPresenter providePresenter() {
        return new RepoPullRequestPresenter();
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

    @Override public void onUpdateCount(int totalCount) {
        if (tabsBadgeListener != null) tabsBadgeListener.onSetBadge(getPresenter().getIssueState() == IssueState.open ? 0 : 1, totalCount);
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, getIssueState());
    }

    @Override public void onClick(View view) {
        onRefresh();
    }

    private IssueState getIssueState() {
        return ((IssueState) getArguments().getSerializable(BundleConstant.EXTRA_TWO));
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
