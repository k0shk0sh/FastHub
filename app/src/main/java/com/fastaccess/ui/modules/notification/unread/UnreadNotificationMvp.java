package com.fastaccess.ui.modules.notification.unread;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.GroupedNotificationModel;
import com.fastaccess.data.dao.model.Notification;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 25 Apr 2017, 3:51 PM
 */

public interface UnreadNotificationMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener  {
        @CallOnMainThread void onNotifyAdapter(@Nullable List<GroupedNotificationModel> items);

        void onRemove(int position);

        void onReadNotification(@NonNull Notification notification);

        void onClick(@NonNull String url);

        void onNotifyNotificationChanged(@NonNull GroupedNotificationModel notification);
    }

    interface Presenter extends BaseViewHolder.OnItemClickListener<GroupedNotificationModel> {

        void onWorkOffline();

        @NonNull ArrayList<GroupedNotificationModel> getNotifications();

        void onMarkAllAsRead(@NonNull List<GroupedNotificationModel> data);

        void onCallApi();
    }
}
