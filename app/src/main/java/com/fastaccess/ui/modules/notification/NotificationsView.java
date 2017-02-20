package com.fastaccess.ui.modules.notification;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.ui.adapter.NotificationsAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindView;

/**
 * Created by Kosh on 20 Feb 2017, 8:50 PM
 */

public class NotificationsView extends BaseFragment<NotificationsMvp.View, NotificationsPresenter>
        implements NotificationsMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;

    private NotificationsAdapter adapter;

    public static NotificationsView newInstance() {
        return new NotificationsView();
    }
    @Override public void onRefresh() {
        getPresenter().onCallApi();
    }

    @Override public void onNotifyAdapter() {
        hideProgress();
        adapter.notifyDataSetChanged();
    }

    @Override protected int fragmentLayout() {
        return R.layout.small_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new NotificationsAdapter(getPresenter().getNotifications());
        adapter.setListener(getPresenter());
        refresh.setOnRefreshListener(this);
        stateLayout.setOnReloadListener(v -> onRefresh());
        recycler.setEmptyView(stateLayout, refresh);
        recycler.setAdapter(adapter);
        if (savedInstanceState == null || !getPresenter().isApiCalled()) {
            getPresenter().onCallApi();
        }
    }

    @NonNull @Override public NotificationsPresenter providePresenter() {
        return new NotificationsPresenter();
    }

    @Override public void showProgress(@StringRes int resId) {
        refresh.setRefreshing(true);
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
}
