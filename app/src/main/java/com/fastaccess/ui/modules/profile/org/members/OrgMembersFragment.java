package com.fastaccess.ui.modules.profile.org.members;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.UsersAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 03 Dec 2016, 3:56 PM
 */

public class OrgMembersFragment extends BaseFragment<OrgMembersMvp.View, OrgMembersPresenter> implements OrgMembersMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    private OnLoadMore<String> onLoadMore;
    private UsersAdapter adapter;

    public static OrgMembersFragment newInstance(@NonNull String username) {
        OrgMembersFragment view = new OrgMembersFragment();
        view.setArguments(Bundler.start().put(BundleConstant.EXTRA, username).end());
        return view;
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
        return R.layout.small_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) {
            throw new NullPointerException("Bundle is null, username is required");
        }
        stateLayout.setEmptyText(R.string.no_members);
        stateLayout.setOnReloadListener(this);
        refresh.setOnRefreshListener(this);
        recycler.setEmptyView(stateLayout, refresh);
        getLoadMore().initialize(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        adapter = new UsersAdapter(getPresenter().getFollowers());
        adapter.setListener(getPresenter());
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        recycler.addKeyLineDivider();
        if (getPresenter().getFollowers().isEmpty() && !getPresenter().isApiCalled()) {
            onRefresh();
        }
        fastScroller.attachRecyclerView(recycler);
    }

    @NonNull @Override public OrgMembersPresenter providePresenter() {
        return new OrgMembersPresenter();
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

    @NonNull @Override public OnLoadMore<String> getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore<>(getPresenter(), getArguments().getString(BundleConstant.EXTRA));
        }
        return onLoadMore;
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, getArguments().getString(BundleConstant.EXTRA));
    }

    @Override public void onClick(View view) {
        onRefresh();
    }

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
        if (recycler != null) recycler.scrollToPosition(0);
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
