package com.fastaccess.ui.modules.notification.unread;

import android.support.annotation.NonNull;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.GroupedNotificationModel;
import com.fastaccess.data.dao.model.Notification;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.tasks.notification.ReadNotificationService;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Kosh on 25 Apr 2017, 3:55 PM
 */

public class UnreadNotificationsPresenter extends BasePresenter<UnreadNotificationMvp.View> implements UnreadNotificationMvp.Presenter {
    private ArrayList<GroupedNotificationModel> notifications = new ArrayList<>();

    @Override public void onItemClick(int position, View v, GroupedNotificationModel model) {
        if (getView() == null) return;
        Notification item = model.getNotification();
        if (v.getId() == R.id.markAsRead) {
            if (item.isUnread()) markAsRead(position, v, item);
        } else if (v.getId() == R.id.unsubsribe) {
            item.setUnread(false);
            manageDisposable(item.save(item));
            sendToView(view -> view.onRemove(position));
            ReadNotificationService.unSubscribe(v.getContext(), item.getId());
        } else {
            if (item.getSubject() != null && item.getSubject().getUrl() != null) {
                if (item.isUnread() && !PrefGetter.isMarkAsReadEnabled()) {
                    markAsRead(position, v, item);
                }
                if (getView() != null) getView().onClick(item.getSubject().getUrl());
            }
        }
    }

    @Override public void onItemLongClick(int position, View v, GroupedNotificationModel item) {}

    @Override public void onWorkOffline() {
        if (notifications.isEmpty()) {
            manageDisposable(RxHelper.getObservable(Notification.getUnreadNotifications().toObservable())
                    .flatMap(notifications -> Observable.just(GroupedNotificationModel.onlyNotifications(notifications)))
                    .subscribe(models -> sendToView(view -> view.onNotifyAdapter(models))));
        } else {
            sendToView(BaseMvp.FAView::hideProgress);
        }
    }

    @NonNull @Override public ArrayList<GroupedNotificationModel> getNotifications() {
        return notifications;
    }

    @Override public void onMarkAllAsRead(@NonNull List<GroupedNotificationModel> data) {
        manageDisposable(RxHelper.getObservable(Observable.fromIterable(data))
                .filter(group -> group.getType() == GroupedNotificationModel.ROW)
                .filter(group -> group.getNotification() != null && group.getNotification().isUnread())
                .map(GroupedNotificationModel::getNotification)
                .subscribe(notification -> {
                    notification.setUnread(false);
                    manageDisposable(notification.save(notification));
                    sendToView(view -> view.onReadNotification(notification));
                }, this::onError));
    }

    @Override public void onCallApi() {
        Observable<List<GroupedNotificationModel>> observable = RestProvider.getNotificationService(PrefGetter.isEnterprise())
                .getNotifications(ParseDateFormat.getLastWeekDate()).flatMap(response -> {
                    if (response.getItems() != null) manageDisposable(Notification.save(response.getItems()));
                    return Observable.just(GroupedNotificationModel.onlyNotifications(response.getItems()));
                });
        makeRestCall(observable, response -> sendToView(view -> view.onNotifyAdapter(response)));
    }

    private void markAsRead(int position, View v, Notification item) {
        item.setUnread(false);
        manageDisposable(item.save(item));
        sendToView(view -> view.onRemove(position));
        ReadNotificationService.start(v.getContext(), item.getId());
    }

}
