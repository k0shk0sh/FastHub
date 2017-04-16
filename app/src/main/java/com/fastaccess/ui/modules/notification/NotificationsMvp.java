package com.fastaccess.ui.modules.notification;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.model.Notification;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 19 Feb 2017, 7:53 PM
 */

public interface NotificationsMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener {

        @NonNull OnLoadMore getLoadMore();

        @CallOnMainThread void onNotifyAdapter(@Nullable List<Notification> items, int page);

        void onRemove(int position);

        void onTypeChanged(boolean unread);

        void onClick(@NonNull String url);

        void onAskMarkAsReadPermission(int position, long id);
    }

    interface Presenter extends BaseViewHolder.OnItemClickListener<Notification>,
            BaseMvp.PaginationListener {

        void onWorkOffline();

        @NonNull ArrayList<Notification> getNotifications();

        void showAllNotifications(boolean showAll);

        void onReadNotification(@NonNull Context context, @NonNull Bundle bundle);
    }
}
