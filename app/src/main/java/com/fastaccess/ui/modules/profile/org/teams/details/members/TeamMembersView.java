package com.fastaccess.ui.modules.profile.org.teams.details.members;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.UsersAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindView;

/**
 * Created by Kosh on 03 Dec 2016, 3:56 PM
 */

public class TeamMembersView extends BaseFragment<TeamMembersMvp.View, TeamMembersPresenter> implements TeamMembersMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) SwipeRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    private OnLoadMore<Long> onLoadMore;
    private UsersAdapter adapter;

    public static TeamMembersView newInstance(@NonNull Long id) {
        TeamMembersView view = new TeamMembersView();
        view.setArguments(Bundler.start().put(BundleConstant.EXTRA, id).end());
        return view;
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
            throw new NullPointerException("Bundle is null, username is required");
        }
        stateLayout.setEmptyText(R.string.no_members);
        stateLayout.setOnReloadListener(this);
        refresh.setOnRefreshListener(this);
        recycler.setEmptyView(stateLayout, refresh);
        getLoadMore().setCurrent_page(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        adapter = new UsersAdapter(getPresenter().getFollowers());
        adapter.setListener(getPresenter());
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        recycler.addKeyLineDivider();
        if (getPresenter().getFollowers().isEmpty() && !getPresenter().isApiCalled()) {
            onRefresh();
        }
    }

    @NonNull @Override public TeamMembersPresenter providePresenter() {
        return new TeamMembersPresenter();
    }

    @Override public void showProgress(@StringRes int resId) {
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

    @NonNull @Override public OnLoadMore<Long> getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore<>(getPresenter(), getArguments().getLong(BundleConstant.EXTRA));
        }
        return onLoadMore;
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, getArguments().getLong(BundleConstant.EXTRA));
    }

    @Override public void onClick(View view) {
        onRefresh();
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
