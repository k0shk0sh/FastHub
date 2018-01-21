package com.fastaccess.ui.modules.feeds;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.GitCommitModel;
import com.fastaccess.data.dao.NameParser;
import com.fastaccess.data.dao.SimpleUrlsModel;
import com.fastaccess.data.dao.model.Event;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.adapter.FeedsAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerActivity;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.ListDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

public class FeedsFragment extends BaseFragment<FeedsMvp.View, FeedsPresenter> implements FeedsMvp.View {

    public static final String TAG = FeedsFragment.class.getSimpleName();

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    private FeedsAdapter adapter;
    private OnLoadMore onLoadMore;

    public static FeedsFragment newInstance(@Nullable String user) {
        return newInstance(user, false);
    }

    public static FeedsFragment newInstance(@Nullable String user, boolean isOrg) {
        FeedsFragment feedsFragment = new FeedsFragment();
        feedsFragment.setArguments(Bundler.start()
                .put(BundleConstant.EXTRA, user)
                .put(BundleConstant.EXTRA_TWO, isOrg)
                .end());
        return feedsFragment;
    }

    @Override protected int fragmentLayout() {
        return R.layout.micro_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        stateLayout.setEmptyText(R.string.no_feeds);
        stateLayout.setOnReloadListener(this);
        refresh.setOnRefreshListener(this);
        recycler.setEmptyView(stateLayout, refresh);
        adapter = new FeedsAdapter(getPresenter().getEvents(), isProfile());
        adapter.setListener(getPresenter());
        getLoadMore().initialize(getPresenter().getCurrentPage(), getPresenter()
                .getPreviousTotal());
        recycler.setAdapter(adapter);
        if (isProfile()) {
            recycler.addDivider();
        }
        recycler.addOnScrollListener(getLoadMore());
        fastScroller.attachRecyclerView(recycler);
        if (getPresenter().getEvents().isEmpty() && !getPresenter().isApiCalled()) {
            getPresenter().onFragmentCreated(getArguments());
        }
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1);
    }

    @Override public void onNotifyAdapter(@Nullable List<Event> items, int page) {
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

    @Override public void onOpenRepoChooser(@NonNull ArrayList<SimpleUrlsModel> models) {
        ListDialogView<SimpleUrlsModel> dialogView = new ListDialogView<>();
        dialogView.initArguments(getString(R.string.repo_chooser), models);
        dialogView.show(getChildFragmentManager(), "ListDialogView");
    }

    @NonNull @Override public FeedsPresenter providePresenter() {
        return new FeedsPresenter();
    }

    @SuppressWarnings("unchecked") @NonNull @Override public OnLoadMore getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore(getPresenter());
        }
        return onLoadMore;
    }

    @Override public void onOpenCommitChooser(@NonNull List<GitCommitModel> commits) {
        ListDialogView<GitCommitModel> dialogView = new ListDialogView<>();
        dialogView.initArguments(getString(R.string.commits), commits);
        dialogView.show(getChildFragmentManager(), "ListDialogView");
    }

    @Override public void onDestroyView() {
        recycler.removeOnScrollListener(getLoadMore());
        super.onDestroyView();
    }

    @Override public void onClick(View view) {
        onRefresh();
    }

    @Override public void onItemSelected(Parcelable item) {
        if (item instanceof SimpleUrlsModel) {
            SchemeParser.launchUri(getContext(), Uri.parse(((SimpleUrlsModel) item).getItem()));
        } else if (item instanceof GitCommitModel) {
            GitCommitModel model = (GitCommitModel) item;
            NameParser nameParser = new NameParser(model.getUrl());
            Intent intent = CommitPagerActivity.createIntent(getContext(), nameParser.getName(),
                    nameParser.getUsername(), model.getSha(), true, LinkParserHelper.isEnterprise(model.getUrl()));
            getContext().startActivity(intent);
        }
    }

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
        if (recycler != null) {
            recycler.scrollToPosition(0);
        }
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }

    public boolean isProfile() {
        return !InputHelper.isEmpty(getArguments().getString(BundleConstant.EXTRA)) &&
                !getArguments().getBoolean(BundleConstant.EXTRA_TWO);
    }
}
