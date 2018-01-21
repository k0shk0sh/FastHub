package com.fastaccess.ui.modules.repos.code.commit;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.BranchesModel;
import com.fastaccess.data.dao.model.Commit;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.CommitsAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;
import com.fastaccess.ui.modules.repos.extras.branches.pager.BranchesPagerFragment;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 03 Dec 2016, 3:56 PM
 */

public class RepoCommitsFragment extends BaseFragment<RepoCommitsMvp.View, RepoCommitsPresenter> implements RepoCommitsMvp.View {
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    @BindView(R.id.branches) FontTextView branches;
    private OnLoadMore onLoadMore;
    private CommitsAdapter adapter;
    private RepoPagerMvp.TabsBadgeListener tabsBadgeListener;

    public static RepoCommitsFragment newInstance(@NonNull String repoId, @NonNull String login, @NonNull String branch) {
        return newInstance(repoId, login, branch, null);
    }

    public static RepoCommitsFragment newInstance(@NonNull String repoId, @NonNull String login, @NonNull String branch,
                                                  @Nullable String path) {
        return newInstance(Bundler.start()
                .put(BundleConstant.ID, repoId)
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.EXTRA_TWO, branch)
                .put(BundleConstant.EXTRA_THREE, path)
                .end());
    }

    public static RepoCommitsFragment newInstance(@NonNull Bundle bundle) {
        RepoCommitsFragment view = new RepoCommitsFragment();
        view.setArguments(bundle);
        return view;
    }

    @OnClick(R.id.branches) void onBranchesClicked() {
        BranchesPagerFragment.Companion.newInstance(getPresenter().login, getPresenter().repoId)
                .show(getChildFragmentManager(), "BranchesFragment");
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RepoPagerMvp.TabsBadgeListener) {
            tabsBadgeListener = (RepoPagerMvp.TabsBadgeListener) context;
        } else if (getParentFragment() instanceof RepoPagerMvp.TabsBadgeListener) {
            tabsBadgeListener = (RepoPagerMvp.TabsBadgeListener) getParentFragment();
        }
    }

    @Override public void onDetach() {
        tabsBadgeListener = null;
        super.onDetach();
    }

    @Override public void onNotifyAdapter(@Nullable List<Commit> items, int page) {
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
        return R.layout.commit_with_branch_layout;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            throw new NullPointerException("Bundle is null, therefore, issues can't be proceeded.");
        }
        stateLayout.setEmptyText(R.string.no_commits);
        stateLayout.setOnReloadListener(this);
        refresh.setOnRefreshListener(this);
        recycler.setEmptyView(stateLayout, refresh);
        recycler.addKeyLineDivider();
        adapter = new CommitsAdapter(getPresenter().getCommits());
        adapter.setListener(getPresenter());
        getLoadMore().initialize(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        if (savedInstanceState == null) {
            getPresenter().onFragmentCreated(getArguments());
        } else if (getPresenter().getCommits().isEmpty() && !getPresenter().isApiCalled()) {
            onRefresh();
        }
        branches.setText(getPresenter().branch);
        fastScroller.attachRecyclerView(recycler);
    }

    @NonNull @Override public RepoCommitsPresenter providePresenter() {
        return new RepoCommitsPresenter();
    }

    @Override public void showProgress(@StringRes int resId) {

        refresh.setRefreshing(true);

        stateLayout.showProgress();
    }

    @Override public void hideProgress() {
        refresh.setRefreshing(false);
        stateLayout.hideProgress();
    }

    @Override public void showErrorMessage(@NonNull String message) {
        showReload();
        super.showErrorMessage(message);
    }

    @Override public void showMessage(int titleRes, int msgRes) {
        showReload();
        super.showMessage(titleRes, msgRes);
    }

    @SuppressWarnings("unchecked") @NonNull @Override public OnLoadMore getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore(getPresenter());
        }
        return onLoadMore;
    }

    @Override public void setBranchesData(@Nullable List<BranchesModel> branchesData, boolean firstTime) {
    }

    @Override public void onShowCommitCount(long sum) {
        if (tabsBadgeListener != null) {
            tabsBadgeListener.onSetBadge(2, (int) sum);
        }
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, null);
    }

    @Override public void onClick(View view) {
        onRefresh();
    }

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
        if (recycler != null) recycler.scrollToPosition(0);
    }

    @Override public void onBranchSelected(@NonNull BranchesModel branch) {
        String ref = branch.getName();
        branches.setText(ref);
        getPresenter().onBranchChanged(ref);
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
