package com.fastaccess.ui.modules.repos.pull_requests.pull_request;

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
import com.fastaccess.helper.Logger;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.PullRequestAdapter;
import com.fastaccess.ui.base.BaseFragment;
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

    public static RepoPullRequestView newInstance(@NonNull String repoId, @NonNull String login, @NonNull IssueState issueState) {
        RepoPullRequestView view = new RepoPullRequestView();
        view.setArguments(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, issueState)
                .end());
        return view;
    }

    @Override public void onNotifyAdapter() {
        Logger.e();
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
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
        super.showErrorMessage(message);
    }

    @NonNull @Override public OnLoadMore<IssueState> getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore<>(getPresenter());
        }
        return onLoadMore;
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, null);
    }

    @Override public void onClick(View view) {
        onRefresh();
    }
}
