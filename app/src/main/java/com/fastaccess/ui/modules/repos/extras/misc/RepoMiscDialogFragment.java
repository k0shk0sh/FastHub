package com.fastaccess.ui.modules.repos.extras.misc;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.UsersAdapter;
import com.fastaccess.ui.base.BaseDialogFragment;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 04 May 2017, 8:41 PM
 */

public class RepoMiscDialogFragment extends BaseDialogFragment<RepoMiscMVp.View, RepoMiscPresenter> implements RepoMiscMVp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    @BindView(R.id.toolbar) Toolbar toolbar;
    private OnLoadMore<Integer> onLoadMore;
    private UsersAdapter adapter;

    private static RepoMiscDialogFragment newInstance(@NonNull String owner, @NonNull String repo, @RepoMiscMVp.MiscType int type) {
        RepoMiscDialogFragment view = new RepoMiscDialogFragment();
        view.setArguments(Bundler.start()
                .put(BundleConstant.EXTRA, owner)
                .put(BundleConstant.ID, repo)
                .put(BundleConstant.EXTRA_TYPE, type)
                .end());
        return view;
    }

    public static void show(@NonNull FragmentManager fragmentManager, @NonNull String owner,
                            @NonNull String repo, @RepoMiscMVp.MiscType int type) {
        newInstance(owner, repo, type).show(fragmentManager, RepoMiscDialogFragment.class.getName());
    }

    @Override public void onNotifyAdapter(@Nullable List<User> items, int page) {
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
        return R.layout.milestone_dialog_layout;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            throw new NullPointerException("Bundle is null, username is required");
        }
        switch (getPresenter().getType()) {
            case RepoMiscMVp.FORKS:
                toolbar.setTitle(R.string.forks);
                stateLayout.setEmptyText(String.format("%s %s", getString(R.string.no), getString(R.string.forks)));
                break;
            case RepoMiscMVp.STARS:
                toolbar.setTitle(R.string.stars);
                stateLayout.setEmptyText(String.format("%s %s", getString(R.string.no), getString(R.string.stars)));
                break;
            case RepoMiscMVp.WATCHERS:
                toolbar.setTitle(R.string.watchers);
                stateLayout.setEmptyText(String.format("%s %s", getString(R.string.no), getString(R.string.watchers)));
                break;
        }
        toolbar.setNavigationIcon(R.drawable.ic_clear);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        stateLayout.setOnReloadListener(v -> getPresenter().onCallApi(1, null));
        refresh.setOnRefreshListener(() -> getPresenter().onCallApi(1, null));
        recycler.setEmptyView(stateLayout, refresh);
        getLoadMore().initialize(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        adapter = new UsersAdapter(getPresenter().getList());
        adapter.setListener(getPresenter());
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        recycler.addKeyLineDivider();
        if (getPresenter().getList().isEmpty() && !getPresenter().isApiCalled()) {
            getPresenter().onCallApi(1, null);
        }
        fastScroller.attachRecyclerView(recycler);
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

    @NonNull @Override public OnLoadMore<Integer> getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore<>(getPresenter());
        }
        return onLoadMore;
    }

    @NonNull @Override public RepoMiscPresenter providePresenter() {
        return new RepoMiscPresenter(getArguments());
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
