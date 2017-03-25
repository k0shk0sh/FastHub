package com.fastaccess.ui.modules.notification;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.model.Notification;
import com.fastaccess.provider.rest.loadmore.OnLoadMore;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;

import java.util.ArrayList;

/**
 * Created by Kosh on 19 Feb 2017, 7:53 PM
 */

public interface NotificationsMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener {

        @NonNull OnLoadMore getLoadMore();

      @CallOnMainThread void onNotifyAdapter();

        void onTypeChanged(boolean unread);

        void onClick(@NonNull String url);
    }

    interface Presenter extends BaseViewHolder.OnItemClickListener<Notification>,
            BaseMvp.PaginationListener {

        void onWorkOffline();

        @NonNull ArrayList<Notification> getNotifications();

        void showAllNotifications(boolean showAll);
    }
}
