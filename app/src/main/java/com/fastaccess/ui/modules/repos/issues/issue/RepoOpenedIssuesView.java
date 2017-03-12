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
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.Logger;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.IssuesAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.repos.issues.RepoIssuesPagerMvp;
import com.fastaccess.ui.modules.repos.issues.create.CreateIssueView;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindView;

/**
 * Created by Kosh on 03 Dec 2016, 3:56 PM
 */

public class RepoOpenedIssuesView extends BaseFragment<RepoIssuesMvp.View, RepoIssuesPresenter> implements RepoIssuesMvp.View {
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    private OnLoadMore<IssueState> onLoadMore;
    private IssuesAdapter adapter;
    private final IssueState issueState = IssueState.open;
    private RepoIssuesPagerMvp.View pagerCallback;

    public static RepoOpenedIssuesView newInstance(@NonNull String repoId, @NonNull String login) {
        RepoOpenedIssuesView view = new RepoOpenedIssuesView();
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
    }

    @Override public void onDetach() {
        pagerCallback = null;
        super.onDetach();
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
        recycler.setEmptyView(stateLayout, refresh);
        stateLayout.setOnReloadListener(this);
        refresh.setOnRefreshListener(this);
        adapter = new IssuesAdapter(getPresenter().getIssues(), true);
        adapter.setListener(getPresenter());
        getLoadMore().setCurrent_page(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        if (savedInstanceState == null) {
            getPresenter().onFragmentCreated(getArguments(), issueState);
        } else if (getPresenter().getIssues().isEmpty() && !getPresenter().isApiCalled()) {
            onRefresh();
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == BundleConstant.REQUEST_CODE) {
            onRefresh();
            if (pagerCallback != null) pagerCallback.setCurrentItem(0);
        }
    }

    @NonNull @Override public RepoIssuesPresenter providePresenter() {
        return new RepoIssuesPresenter();
    }

    @Override public void hideProgress() {
        refresh.setRefreshing(false);
        stateLayout.hideProgress();
    }

    @Override public void showProgress(@StringRes int resId) {

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
        onLoadMore.setParameter(issueState);
        return onLoadMore;
    }

    @Override public void onAddIssue() {
        CreateIssueView.startForResult(this, getPresenter().login(), getPresenter().repoId());
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, null);
    }

    @Override public void onClick(View view) {
        onRefresh();
    }
}
