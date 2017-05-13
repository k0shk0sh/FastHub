package com.fastaccess.ui.modules.notification.all;

import android.support.annotation.NonNull;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.GroupedNotificationModel;
import com.fastaccess.data.dao.model.Notification;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.tasks.notification.ReadNotificationService;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by Kosh on 20 Feb 2017, 8:46 PM
 */

public class AllNotificationsPresenter extends BasePresenter<AllNotificationsMvp.View> implements AllNotificationsMvp.Presenter {
    private ArrayList<GroupedNotificationModel> notifications = new ArrayList<>();

    @Override public void onItemClick(int position, View v, GroupedNotificationModel model) {
        if (getView() == null) return;
        if (model.getType() == GroupedNotificationModel.ROW) {
            Notification item = model.getNotification();
            if (v.getId() == R.id.markAsRead) {
                if (item.isUnread() && !PrefGetter.isMarkAsReadEnabled()) {
                    markAsRead(position, v, item);
                }
            } else if (v.getId() == R.id.unsubsribe) {
                item.setUnread(false);
                manageSubscription(item.save(item).subscribe());
                sendToView(view -> view.onUpdateReadState(new GroupedNotificationModel(item), position));
                ReadNotificationService.unSubscribe(v.getContext(), item.getId());
            } else {
                if (item.getSubject() != null && item.getSubject().getUrl() != null) {
                    if (item.isUnread() && !PrefGetter.isMarkAsReadEnabled()) {
                        markAsRead(position, v, item);
                    }
                    if (getView() != null) getView().onClick(item.getSubject().getUrl());
                }
            }
        } else {
            if (v.getId() == R.id.markAsRead) {
                Repo repo = model.getRepo();
                if (repo == null) return;
                getView().onMarkAllByRepo(repo);
            }
        }
    }

    private void markAsRead(int position, View v, Notification item) {
        item.setUnread(false);
        manageSubscription(item.save(item).subscribe());
        sendToView(view -> view.onUpdateReadState(new GroupedNotificationModel(item), position));
        ReadNotificationService.start(v.getContext(), item.getId());
    }

    @Override public void onItemLongClick(int position, View v, GroupedNotificationModel item) {}

    @Override public void onError(@NonNull Throwable throwable) {
        onWorkOffline();
        super.onError(throwable);
    }

    @Override public void onWorkOffline() {
        if (notifications.isEmpty()) {
            manageSubscription(RxHelper.getObserver(Notification.getAlltNotifications())
                    .flatMap(notifications -> Observable.from(GroupedNotificationModel.construct(notifications)).toList())
                    .subscribe(models -> sendToView(view -> view.onNotifyAdapter(models))));
        } else {
            sendToView(BaseMvp.FAView::hideProgress);
        }
    }

    @NonNull @Override public ArrayList<GroupedNotificationModel> getNotifications() {
        return notifications;
    }

    @Override public void onCallApi() {
//        Observable<List<Notification>> notifications = RestProvider.getNotificationService().getAllNotifications()
//                .flatMap(response -> response.getItems() != null ? Observable.from(response.getItems()) : Observable.empty())
//                .filter(ObjectsCompat::nonNull)
//                .flatMap(notification -> RestProvider.getNotificationService().isSubscribed(notification.getId())
//                                .onErrorReturn(throwable -> null),
//                        (notification, subscriptionModel) -> {
//                            if (subscriptionModel != null) {
//                                notification.setIsSubscribed(subscriptionModel.isSubscribed());
//                            } else {
//                                notification.setIsSubscribed(true);
//                            }
//                            return notification;
//                        })
//                .toList();
        Observable<List<GroupedNotificationModel>> observable = RestProvider.getNotificationService().getAllNotifications()
                .flatMap(response -> {
                    if (response.getItems() != null) manageSubscription(Notification.save(response.getItems()).subscribe());
                    return Observable.just(GroupedNotificationModel.construct(response.getItems()));
                });
        makeRestCall(observable, response -> sendToView(view -> view.onNotifyAdapter(response)));
    }

    @Override public void onMarkAllAsRead(@NonNull List<GroupedNotificationModel> data) {
        manageSubscription(RxHelper.getObserver(Observable.from(data))
                .filter(group -> group.getType() == GroupedNotificationModel.ROW)
                .filter(group -> group.getNotification() != null && group.getNotification().isUnread())
                .map(GroupedNotificationModel::getNotification)
                .subscribe(notification -> {
                    Logger.e(notification.getUrl());
                    notification.setUnread(false);
                    manageSubscription(notification.save(notification).subscribe());
                    sendToView(view -> view.onReadNotification(notification));
                }));
    }

    @Override public void onMarkReadByRepo(@NonNull List<GroupedNotificationModel> data, @NonNull Repo repo) {
        manageSubscription(RxHelper.getObserver(Observable.from(data))
                .filter(group -> group.getType() == GroupedNotificationModel.ROW)
                .filter(group -> group.getNotification() != null && group.getNotification().isUnread())
                .filter(group -> group.getNotification().getRepository().getFullName().equalsIgnoreCase(repo.getFullName()))
                .map(GroupedNotificationModel::getNotification)
                .subscribe(notification -> {
                    Logger.e(notification.getUrl());
                    notification.setUnread(false);
                    manageSubscription(notification.save(notification).subscribe());
                    sendToView(view -> view.onReadNotification(notification));
                }));
    }
}
