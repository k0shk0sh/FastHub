package com.fastaccess.ui.modules.notification;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;

import com.fastaccess.data.dao.NotificationThreadModel;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by Kosh on 20 Feb 2017, 8:46 PM
 */

public class NotificationsPresenter extends BasePresenter<NotificationsMvp.View> implements NotificationsMvp.Presenter {
    private ArrayList<NotificationThreadModel> notifications = new ArrayList<>();

    @Override public void onItemClick(int position, View v, NotificationThreadModel item) {
        if (item.isUnread()) {
            makeRestCall(RestProvider.getNotificationService()
                            .markAsRead(String.valueOf(item.getId())),
                    booleanResponse -> {
                        item.setUnread(booleanResponse.code() == 205);
                        manageSubscription(item.save().subscribe());
                        notifications.remove(position);
                        sendToView(NotificationsMvp.View::onNotifyAdapter);
                    });
        }
        if (item.getSubject() != null) {
            SchemeParser.launchUri(v.getContext(), Uri.parse(item.getSubject().getUrl()));
        }
    }

    @Override public void onItemLongClick(int position, View v, NotificationThreadModel item) {
        onItemClick(position, v, item);
    }

    @Override public <T> T onError(@NonNull Throwable throwable, @NonNull Observable<T> observable) {
        onWorkOffline();
        return super.onError(throwable, observable);
    }

    @Override public void onCallApi() {
        makeRestCall(RestProvider.getNotificationService().getNotifications(),
                response -> {
                    notifications.clear();
                    if (response.getItems() != null) {
                        manageSubscription(NotificationThreadModel.save(response.getItems()).subscribe());
                        notifications.addAll(response.getItems());
                    }
                    sendToView(NotificationsMvp.View::onNotifyAdapter);
                });
    }

    @Override public void onWorkOffline() {
        if (notifications.isEmpty()) {
            manageSubscription(NotificationThreadModel.getNotifications()
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

    @NonNull @Override public ArrayList<NotificationThreadModel> getNotifications() {
        return notifications;
    }
}
