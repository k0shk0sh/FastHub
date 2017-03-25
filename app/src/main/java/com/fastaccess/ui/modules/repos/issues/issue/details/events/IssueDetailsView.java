package com.fastaccess.ui.modules.repos.issues.issue.details.events;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.adapter.IssueTimelineAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindView;

/**
 * Created by Kosh on 13 Dec 2016, 12:40 AM
 */

public class IssueDetailsView extends BaseFragment<IssueDetailsMvp.View, IssueDetailsPresenter> implements IssueDetailsMvp.View {
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    private IssueTimelineAdapter adapter;
    private OnLoadMore onLoadMore;

    public static IssueDetailsView newInstance(@NonNull Issue issueModel) {
        IssueDetailsView view = new IssueDetailsView();
        view.setArguments(Bundler.start().put(BundleConstant.ITEM, issueModel).end());
        return view;
    }

    @Override protected int fragmentLayout() {
        return R.layout.small_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) getPresenter().onFragmentCreated(getArguments());
        stateLayout.setEmptyText(R.string.no_events);
        recycler.setEmptyView(stateLayout, refresh);
        refresh.setOnRefreshListener(this);
        stateLayout.setOnReloadListener(this);
        adapter = new IssueTimelineAdapter(getPresenter().getEvents());
        adapter.setListener(getPresenter());
        getLoadMore().setCurrent_page(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        if (getPresenter().getEvents().size() == 1 && !getPresenter().isApiCalled()) {
            onRefresh();
        }
    }

    @NonNull @Override public IssueDetailsPresenter providePresenter() {
        return new IssueDetailsPresenter();
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, null);
    }

    @Override public void onNotifyAdapter() {
        hideProgress();
        adapter.notifyDataSetChanged();
    }

    @Override public void showProgress(@StringRes int resId) {

        stateLayout.showProgress();
    }

    @Override public void hideProgress() {
        refresh.setRefreshing(false);
        stateLayout.hideProgress();
    }

    @Override public void showErrorMessage(@NonNull String msgRes) {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
        super.showErrorMessage(msgRes);
    }

    @SuppressWarnings("unchecked") @NonNull @Override public OnLoadMore getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore(getPresenter());
        }
        return onLoadMore;
    }

    @Override public void onClick(View view) {
        onRefresh();
    }
}
