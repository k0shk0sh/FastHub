package com.fastaccess.ui.modules.notification.all;

import android.support.annotation.NonNull;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.GroupedNotificationModel;
import com.fastaccess.data.dao.NameParser;
import com.fastaccess.data.dao.model.Notification;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.tasks.notification.ReadNotificationService;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.repos.RepoPagerActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Kosh on 20 Feb 2017, 8:46 PM
 */

public class AllNotificationsPresenter extends BasePresenter<AllNotificationsMvp.View> implements AllNotificationsMvp.Presenter {
    private final ArrayList<GroupedNotificationModel> notifications = new ArrayList<>();

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
                manageDisposable(item.save(item));
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
            Repo repo = model.getRepo();
            if (repo == null) return;
            if (v.getId() == R.id.markAsRead) {
                getView().onMarkAllByRepo(repo);
            } else {
                RepoPagerActivity.startRepoPager(v.getContext(), new NameParser(repo.getUrl()));
            }
        }
    }

    private void markAsRead(int position, View v, Notification item) {
        item.setUnread(false);
        manageDisposable(item.save(item));
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
            manageDisposable(RxHelper.getObservable(Notification.getAllNotifications().toObservable())
                    .flatMap(notifications -> Observable.just(GroupedNotificationModel.construct(notifications)))
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
//                .flatMap(response -> response.getItems() != null ? Observable.fromIterable(response.getItems()) : Observable.empty())
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
        Observable<List<GroupedNotificationModel>> observable = RestProvider.getNotificationService(PrefGetter.isEnterprise())
                .getAllNotifications().flatMap(response -> {
                    manageDisposable(Notification.save(response.getItems()));
                    if (response.getItems() != null && !response.getItems().isEmpty()) {
                        return Observable.just(GroupedNotificationModel.construct(response.getItems()));
                    }
                    return Observable.empty();
                });
        makeRestCall(observable.doOnComplete(() -> sendToView(BaseMvp.FAView::hideProgress)), response -> sendToView(view -> view.onNotifyAdapter
                (response)));
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

    @Override public void onMarkReadByRepo(@NonNull List<GroupedNotificationModel> data, @NonNull Repo repo) {
        manageDisposable(RxHelper.getObservable(Observable.fromIterable(data))
                .filter(group -> group.getType() == GroupedNotificationModel.ROW)
                .filter(group -> group.getNotification() != null && group.getNotification().isUnread())
                .filter(group -> group.getNotification().getRepository().getFullName().equalsIgnoreCase(repo.getFullName()))
                .map(GroupedNotificationModel::getNotification)
                .subscribe(notification -> {
                    notification.setUnread(false);
                    manageDisposable(notification.save(notification));
                    sendToView(view -> view.onReadNotification(notification));
                }, this::onError));
    }
}
