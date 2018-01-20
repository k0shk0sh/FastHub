package com.fastaccess.ui.modules.notification.all;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.GroupedNotificationModel;
import com.fastaccess.data.dao.model.Notification;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.ObjectsCompat;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.provider.tasks.notification.ReadNotificationService;
import com.fastaccess.ui.adapter.NotificationsAdapter;
import com.fastaccess.ui.adapter.viewholder.NotificationsViewHolder;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.notification.callback.OnNotificationChangedListener;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 20 Feb 2017, 8:50 PM
 */

public class AllNotificationsFragment extends BaseFragment<AllNotificationsMvp.View, AllNotificationsPresenter>
        implements AllNotificationsMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;
    @BindView(R.id.fastScroller) RecyclerViewFastScroller fastScroller;
    private NotificationsAdapter adapter;
    private OnNotificationChangedListener onNotificationChangedListener;

    public static AllNotificationsFragment newInstance() {
        return new AllNotificationsFragment();
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNotificationChangedListener) {
            onNotificationChangedListener = (OnNotificationChangedListener) context;
        }
    }

    @Override public void onDetach() {
        onNotificationChangedListener = null;
        super.onDetach();
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi();
    }

    @Override public void onUpdateReadState(GroupedNotificationModel item, int position) {
        if (onNotificationChangedListener != null) onNotificationChangedListener.onNotificationChanged(item, 0);
        adapter.swapItem(item, position);
    }

    @Override public void onNotifyAdapter(@Nullable List<GroupedNotificationModel> items) {
        hideProgress();
        if (items == null || items.isEmpty()) {
            adapter.clear();
            return;
        }
        adapter.insertItems(items);
        if (isSafe()) getActivity().invalidateOptionsMenu();
    }

    @Override public void onClick(@NonNull String url) {
        SchemeParser.launchUri(getContext(), Uri.parse(url), true);
    }

    @Override public void onReadNotification(@NonNull Notification notification) {
        GroupedNotificationModel model = new GroupedNotificationModel(notification);
        if (onNotificationChangedListener != null) onNotificationChangedListener.onNotificationChanged(model, 0);
        adapter.swapItem(model);
        ReadNotificationService.start(getContext(), notification.getId());
    }

    @Override public void onMarkAllByRepo(@NonNull Repo repo) {
        getPresenter().onMarkReadByRepo(adapter.getData(), repo);
    }

    @Override public void onNotifyNotificationChanged(@NonNull GroupedNotificationModel notification) {
        if (adapter != null) {
            adapter.swapItem(notification);
        }
    }

    @Override protected int fragmentLayout() {
        return R.layout.micro_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new NotificationsAdapter(getPresenter().getNotifications(), true, true);
        adapter.setListener(getPresenter());
        refresh.setOnRefreshListener(this);
        stateLayout.setEmptyText(R.string.no_notifications);
        stateLayout.setOnReloadListener(v -> onRefresh());
        recycler.setEmptyView(stateLayout, refresh);
        recycler.setAdapter(adapter);
        recycler.addDivider(NotificationsViewHolder.class);
        if (savedInstanceState == null || !getPresenter().isApiCalled()) {
            onRefresh();
        }
        fastScroller.attachRecyclerView(recycler);
    }

    @NonNull @Override public AllNotificationsPresenter providePresenter() {
        return new AllNotificationsPresenter();
    }

    @Override public void showProgress(@StringRes int resId) {
        refresh.setRefreshing(true);
        stateLayout.showProgress();
    }

    @Override public void hideProgress() {
        refresh.setRefreshing(false);
        stateLayout.hideProgress();
        stateLayout.showReload(adapter.getItemCount());
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
                MessageDialogView.newInstance(getString(R.string.mark_all_as_read), getString(R.string.confirm_message),
                        false, false, Bundler.start()
                                .put("primary_button", getString(R.string.yes))
                                .put("secondary_button", getString(R.string.no))
                                .end())
                        .show(getChildFragmentManager(), MessageDialogView.TAG);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onPrepareOptionsMenu(Menu menu) {
        boolean hasUnread = Stream.of(adapter.getData())
                .filter(ObjectsCompat::nonNull)
                .filter(group -> group.getType() == GroupedNotificationModel.ROW)
                .anyMatch(group -> group.getNotification().isUnread());
        menu.findItem(R.id.readAll).setVisible(hasUnread);
        super.onPrepareOptionsMenu(menu);
    }

    @Override public void onScrollTop(int index) {
        super.onScrollTop(index);
        if (recycler != null) recycler.scrollToPosition(0);
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (isOk) {
            getPresenter().onMarkAllAsRead(adapter.getData());
        }
    }

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }
}
