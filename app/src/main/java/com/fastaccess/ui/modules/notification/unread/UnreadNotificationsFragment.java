package com.fastaccess.ui.modules.notification.unread;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.GroupedNotificationModel;
import com.fastaccess.data.dao.model.Notification;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.provider.tasks.notification.ReadNotificationService;
import com.fastaccess.ui.adapter.NotificationsAdapter;
import com.fastaccess.ui.adapter.viewholder.NotificationsViewHolder;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 25 Apr 2017, 4:06 PM
 */

public class UnreadNotificationsFragment extends BaseFragment<UnreadNotificationMvp.View, UnreadNotificationsPresenter>
        implements UnreadNotificationMvp.View {
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    private NotificationsAdapter adapter;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public void onNotifyAdapter(@Nullable List<GroupedNotificationModel> items) {
        hideProgress();
        if (items == null || items.isEmpty()) {
            adapter.clear();
            return;
        }
        adapter.insertItems(items);
        invalidateMenu();
    }

    @Override public void onRemove(int position) {
        hideProgress();
        adapter.removeItem(position);
        invalidateMenu();
    }

    @Override public void onReadNotification(@NonNull Notification notification) {
        adapter.removeItem(new GroupedNotificationModel(notification));
        ReadNotificationService.start(getContext(), notification.getId());
        invalidateMenu();
    }

    @Override public void onClick(@NonNull String url) {
        SchemeParser.launchUri(getContext(), Uri.parse(url), true);
    }

    @Override protected int fragmentLayout() {
        return R.layout.small_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new NotificationsAdapter(getPresenter().getNotifications(), false);
        adapter.setListener(getPresenter());
        refresh.setOnRefreshListener(this);
        stateLayout.setEmptyText(R.string.no_notifications);
        stateLayout.setOnReloadListener(v -> onRefresh());
        recycler.setEmptyView(stateLayout, refresh);
        recycler.setAdapter(adapter);
        recycler.addKeyLineDivider();
        recycler.addDivider(NotificationsViewHolder.class);
        if (savedInstanceState == null || !getPresenter().isApiCalled()) {
            onRefresh();
        }
    }

    @NonNull @Override public UnreadNotificationsPresenter providePresenter() {
        return new UnreadNotificationsPresenter();
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

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.notification_menu, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.readAll) {
            if (!adapter.getData().isEmpty()) {
                getPresenter().onMarkAllAsRead(adapter.getData());
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onPrepareOptionsMenu(Menu menu) {
        boolean hasUnread = !adapter.getData().isEmpty();
        menu.findItem(R.id.readAll).setVisible(hasUnread);
        super.onPrepareOptionsMenu(menu);
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi();
    }

    private void invalidateMenu() {
        if (isSafe()) getActivity().supportInvalidateOptionsMenu();
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
