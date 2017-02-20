package com.fastaccess.ui.modules.notification;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;

import com.fastaccess.data.dao.NotificationThreadModel;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 19 Feb 2017, 7:53 PM
 */

public interface NotificationsMvp {

    interface View extends BaseMvp.FAView, SwipeRefreshLayout.OnRefreshListener {
        void onNotifyAdapter();
    }

    interface Presenter extends BaseViewHolder.OnItemClickListener<NotificationThreadModel> {

        void onCallApi();

        void onWorkOffline();

        @NonNull ArrayList<NotificationThreadModel> getNotifications();
    }
}
