package com.fastaccess.ui.modules.notification.callback;

import androidx.annotation.NonNull;

import com.fastaccess.data.dao.GroupedNotificationModel;

public interface OnNotificationChangedListener {

    void onNotificationChanged(@NonNull GroupedNotificationModel notification, int index);
}