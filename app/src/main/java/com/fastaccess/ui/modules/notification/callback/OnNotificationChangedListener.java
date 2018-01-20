package com.fastaccess.ui.modules.notification.callback;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.GroupedNotificationModel;

public interface OnNotificationChangedListener {

    void onNotificationChanged(@NonNull GroupedNotificationModel notification, int index);
}