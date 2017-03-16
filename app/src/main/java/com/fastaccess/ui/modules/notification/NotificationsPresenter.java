package com.fastaccess.ui.modules.notification;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.model.Notification;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.tasks.notification.ReadNotificationService;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by Kosh on 20 Feb 2017, 8:46 PM
 */

public class NotificationsPresenter extends BasePresenter<NotificationsMvp.View> implements NotificationsMvp.Presenter {
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    private boolean showAll;
    private ArrayList<Notification> notifications = new ArrayList<>();

    @Override public void onItemClick(int position, View v, Notification item) {
        if (item.isUnread()) {
            ReadNotificationService.start(v.getContext(), item.getId());
            notifications.remove(position);
            sendToView(NotificationsMvp.View::onNotifyAdapter);
            item.setUnread(true);
            manageSubscription(item.save(item).subscribe());
        }
        if (item.getSubject() != null && item.getSubject().getUrl() != null) {
            if (getView() != null) getView().onClick(item.getSubject().getUrl());
        }
    }

    @Override public void onItemLongClick(int position, View v, Notification item) {
        onItemClick(position, v, item);
    }

    @Override public void onError(@NonNull Throwable throwable) {
        onWorkOffline();
        super.onError(throwable);
    }

    @Override public void onWorkOffline() {
        if (notifications.isEmpty()) {
            manageSubscription(RxHelper.getObserver(Notification.getNotifications())
                    .subscribe(models -> {
                        if (models != null) {
                            notifications.addAll(models);
                            sendToView(NotificationsMvp.View::onNotifyAdapter);
                        }
                    }));
        } else {
            sendToView(BaseMvp.FAView::hideProgress);
        }
    }

    @NonNull @Override public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    @Override public void showAllNotifications(boolean showAll) {
        this.showAll = showAll;
    }

    @Override public int getCurrentPage() {
        return page;
    }

    @Override public int getPreviousTotal() {
        return previousTotal;
    }

    @Override public void setCurrentPage(int page) {
        this.page = page;
    }

    @Override public void setPreviousTotal(int previousTotal) {
        this.previousTotal = previousTotal;
    }

    @Override public void onCallApi(int page, @Nullable Object parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || lastPage == 0) {
            sendToView(NotificationsMvp.View::hideProgress);
            return;
        }
        setCurrentPage(page);
        Observable<Pageable<Notification>> observable =
                showAll ? RestProvider.getNotificationService().getAllNotifications(page)
                        : RestProvider.getNotificationService().getNotifications(page);
        makeRestCall(observable, response -> {
            if (response.getItems() != null) {
                lastPage = response.getLast();
                if (page == 1) {
                    notifications.clear();
                    manageSubscription(Notification.save(response.getItems()).subscribe());
                }
                notifications.addAll(response.getItems());
            }
            sendToView(NotificationsMvp.View::onNotifyAdapter);
        });
    }
}
