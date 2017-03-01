package com.fastaccess.ui.modules.notification;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.fastaccess.data.dao.NotificationThreadModel;
import com.fastaccess.data.dao.Pageable;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Kosh on 20 Feb 2017, 8:46 PM
 */

public class NotificationsPresenter extends BasePresenter<NotificationsMvp.View> implements NotificationsMvp.Presenter {
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;
    private boolean showAll;
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
        if (item.getSubject() != null && item.getSubject().getUrl() != null) {
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

    @Override public void onReadAll() {
        if (!notifications.isEmpty()) {
            Observable<Response<Boolean>> observable = Observable.from(notifications)
                    .filter(NotificationThreadModel::isUnread)
                    .flatMap(new Func1<NotificationThreadModel, Observable<Response<Boolean>>>() {
                        @Override public Observable<Response<Boolean>> call(NotificationThreadModel notificationThreadModel) {
                            return RxHelper.getObserver(RestProvider.getNotificationService()
                                    .markAsRead(String.valueOf(notificationThreadModel.getId())));
                        }
                    });
            manageSubscription(observable.isEmpty().subscribe(isEmpty -> {
                if (!isEmpty) {
                    makeRestCall(observable, booleanResponse -> {
                        if (booleanResponse.code() == 205) {
                            if (!notifications.isEmpty()) {
                                notifications.clear();
                                manageSubscription(NotificationThreadModel.deleteTable().observe().subscribe());
                                sendToView(NotificationsMvp.View::onNotifyAdapter);
                            }
                        }
                    });
                }
            }));
        }
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
        Observable<Pageable<NotificationThreadModel>> observable = showAll ? RestProvider.getNotificationService().getAllNotifications(page)
                                                                           : RestProvider.getNotificationService().getNotifications(page);
        makeRestCall(observable, response -> {
            notifications.clear();
            if (response.getItems() != null) {
                lastPage = response.getLast();
                if (page == 1) {
                    notifications.clear();
                    manageSubscription(NotificationThreadModel.save(response.getItems()).subscribe());
                }
                notifications.addAll(response.getItems());
            }
            sendToView(NotificationsMvp.View::onNotifyAdapter);
        });
    }
}
