package com.fastaccess.ui.modules.notification;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.model.Notification;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.helper.PrefGetter;
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
        if (item.isUnread() && !PrefGetter.isMarkAsReadEnabled()) {
            ReadNotificationService.start(v.getContext(), item.getId());
            sendToView(view -> view.onRemove(position));
            item.setUnread(true);
            manageSubscription(item.save(item).subscribe());
        }
        if (item.getSubject() != null && item.getSubject().getUrl() != null) {
            if (getView() != null) getView().onClick(item.getSubject().getUrl());
        }
    }

    @Override public void onItemLongClick(int position, View v, Notification item) {
        if (getView() != null) {
            getView().onAskMarkAsReadPermission(position, item.getId());
        }
    }

    @Override public void onError(@NonNull Throwable throwable) {
        onWorkOffline();
        super.onError(throwable);
    }

    @Override public void onWorkOffline() {
        if (notifications.isEmpty()) {
            manageSubscription(RxHelper.getObserver(Notification.getNotifications())
                    .subscribe(models -> sendToView(view -> view.onNotifyAdapter(models, 1))));
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

    @Override public void onReadNotification(@NonNull Context context, @NonNull Bundle bundle) {
        long id = bundle.getLong(BundleConstant.ID);
        int position = bundle.getInt(BundleConstant.EXTRA);
        Notification notification = notifications.get(position);
        if (notification != null && notification.getId() == id) {
            ReadNotificationService.start(context, id);
            notification.setUnread(true);
            manageSubscription(notification.save(notification).subscribe());
            sendToView(view -> view.onRemove(position));
        }
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
                        : RestProvider.getNotificationService().getNotifications(ParseDateFormat.getDateByDays(-30), page);
        makeRestCall(observable, response -> {
            if (response.getItems() != null) {
                lastPage = response.getLast();
                if (page == 1) {
                    manageSubscription(Notification.save(response.getItems()).subscribe());
                }
            }
            sendToView(view -> view.onNotifyAdapter(response.getItems(), page));
        });
    }
}
