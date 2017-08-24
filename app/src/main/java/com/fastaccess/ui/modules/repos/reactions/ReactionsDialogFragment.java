package com.fastaccess.ui.modules.repos.reactions;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.ReactionTypes;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.provider.timeline.CommentsHelper;
import com.fastaccess.provider.timeline.ReactionsProvider;
import com.fastaccess.ui.adapter.UsersAdapter;
import com.fastaccess.ui.base.BaseDialogFragment;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Apr 2017, 11:30 AM
 */

public class ReactionsDialogFragment extends BaseDialogFragment<ReactionsDialogMvp.View, ReactionsDialogPresenter>
        implements ReactionsDialogMvp.View {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.appbar) AppBarLayout appbar;
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    private UsersAdapter adapter;
    private OnLoadMore onLoadMore;

    public static ReactionsDialogFragment newInstance(@NonNull String login, @NonNull String repoId,
                                                      @NonNull ReactionTypes type, long idOrNumber,
                                                      @ReactionsProvider.ReactionType int reactionType) {
        ReactionsDialogFragment view = new ReactionsDialogFragment();
        view.setArguments(Bundler.start()
                .put(BundleConstant.EXTRA_TYPE, type)
                .put(BundleConstant.EXTRA, repoId)
                .put(BundleConstant.EXTRA_TWO, login)
                .put(BundleConstant.EXTRA_THREE, reactionType)
                .put(BundleConstant.ID, idOrNumber)
                .end());
        return view;
    }

    @Override protected int fragmentLayout() {
        return R.layout.milestone_dialog_layout;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        toolbar.setNavigationIcon(R.drawable.ic_clear);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        stateLayout.setEmptyText(R.string.no_reactions);
        stateLayout.setOnReloadListener(v -> getPresenter().onCallApi(1, null));
        refresh.setOnRefreshListener(() -> getPresenter().onCallApi(1, null));
        recycler.setEmptyView(stateLayout, refresh);
        adapter = new UsersAdapter(getPresenter().getUsers());
        getLoadMore().initialize(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        if (savedInstanceState == null) {
            getPresenter().onFragmentCreated(getArguments());
        }
        toolbar.setTitle(SpannableBuilder.builder().append(getString(R.string.reactions))
                .append(" ")
                .append(CommentsHelper.getEmoji(getPresenter().getReactionType())));
        fastScroller.attachRecyclerView(recycler);
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

    @NonNull @Override public ReactionsDialogPresenter providePresenter() {
        return new ReactionsDialogPresenter();
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
