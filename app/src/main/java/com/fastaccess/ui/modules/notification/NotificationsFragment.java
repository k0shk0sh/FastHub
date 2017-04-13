package com.fastaccess.ui.modules.notification;

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
import com.fastaccess.data.dao.model.Notification;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.Logger;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.provider.scheme.StackBuilderSchemeParser;
import com.fastaccess.provider.tasks.notification.ReadNotificationService;
import com.fastaccess.ui.adapter.NotificationsAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.AppbarRefreshLayout;
import com.fastaccess.ui.widgets.StateLayout;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindView;

/**
 * Created by Kosh on 20 Feb 2017, 8:50 PM
 */

public class NotificationsFragment extends BaseFragment<NotificationsMvp.View, NotificationsPresenter>
        implements NotificationsMvp.View {

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.refresh) AppbarRefreshLayout refresh;
    @BindView(R.id.stateLayout) StateLayout stateLayout;

    private OnLoadMore onLoadMore;
    private NotificationsAdapter adapter;

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public void onRefresh() {
        getPresenter().onCallApi(1, null);
    }

    @SuppressWarnings("unchecked") @NonNull @Override public OnLoadMore getLoadMore() {
        if (onLoadMore == null) {
            onLoadMore = new OnLoadMore<>(getPresenter());
        }
        return onLoadMore;
    }

    @Override public void onNotifyAdapter() {
        hideProgress();
        adapter.notifyDataSetChanged();
    }

    @Override public void onTypeChanged(boolean unread) {
        getPresenter().showAllNotifications(!unread);
        onRefresh();
    }

    @Override public void onClick(@NonNull String url) {
        Logger.e(getActivity().isTaskRoot());
        if (getActivity().isTaskRoot()) {
            StackBuilderSchemeParser.launchUri(getContext(), Uri.parse(url));
        } else {
            SchemeParser.launchUri(getContext(), Uri.parse(url), true);
        }
    }

    @Override public void onAskMarkAsReadPermission(int position, long id) {
        MessageDialogView.newInstance(getString(R.string.marking_as_read), getString(R.string.confirm_message),
                Bundler.start().put(BundleConstant.YES_NO_EXTRA, true)
                        .put(BundleConstant.ID, id)
                        .put(BundleConstant.EXTRA, position)
                        .end())
                .show(getChildFragmentManager(), MessageDialogView.TAG);
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, @Nullable Bundle bundle) {
        super.onMessageDialogActionClicked(isOk, bundle);
        if (isOk && bundle != null) {
            getPresenter().onReadNotification(getContext(), bundle);
        }
    }

    @Override protected int fragmentLayout() {
        return R.layout.small_grid_refresh_list;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new NotificationsAdapter(getPresenter().getNotifications());
        adapter.setListener(getPresenter());
        refresh.setOnRefreshListener(this);
        stateLayout.setEmptyText(R.string.no_notifications);
        stateLayout.setOnReloadListener(v -> onRefresh());
        getLoadMore().setCurrent_page(getPresenter().getCurrentPage(), getPresenter().getPreviousTotal());
        recycler.setEmptyView(stateLayout, refresh);
        recycler.setAdapter(adapter);
        recycler.addOnScrollListener(getLoadMore());
        recycler.addDivider();
        if (savedInstanceState == null || !getPresenter().isApiCalled()) {
            onRefresh();
        }
    }

    @NonNull @Override public NotificationsPresenter providePresenter() {
        return new NotificationsPresenter();
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

    private void showReload() {
        hideProgress();
        stateLayout.showReload(adapter.getItemCount());
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.notification_menu, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.readAll) {
            if (!getPresenter().getNotifications().isEmpty()) {
                long[] ids = Stream.of(getPresenter().getNotifications())
                        .filter(Notification::isUnread)
                        .mapToLong(Notification::getId)
                        .toArray();
                if (ids != null && ids.length > 0) {
                    getPresenter().getNotifications().clear();
                    onNotifyAdapter();
                    ReadNotificationService.start(getContext(), ids);
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroyView() {
        recycler.removeOnScrollListener(getLoadMore());
        super.onDestroyView();
    }
}
