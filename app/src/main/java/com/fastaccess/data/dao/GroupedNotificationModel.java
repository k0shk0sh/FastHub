package com.fastaccess.data.dao;

import android.support.annotation.Nullable;

import com.annimon.stream.Stream;
import com.fastaccess.data.dao.model.Notification;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.helper.InputHelper;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Created by Kosh on 18 Apr 2017, 8:07 PM
 */

@Getter @Setter public class GroupedNotificationModel {
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupedNotificationModel model = (GroupedNotificationModel) o;
        return notification != null && model.getNotification() != null && notification.getId() == (model.notification.getId());
    }

    @Override public int hashCode() {
        return notification != null ? InputHelper.getSafeIntId(notification.getId()) : 0;
    }

    public static final int HEADER = 1;
    public static final int ROW = 2;
    private int type;
    private Repo repo;
    private Notification notification;

    public GroupedNotificationModel(Repo repo) {
        this.type = HEADER;
        this.repo = repo;
    }

    public GroupedNotificationModel(Notification notification) {
        this.type = ROW;
        this.notification = notification;
    }

    @NonNull public static List<GroupedNotificationModel> construct(@Nullable List<Notification> items) {
        List<GroupedNotificationModel> models = new ArrayList<>();
        if (items == null || items.isEmpty()) return models;
        Stream.of(items)
                .groupBy(Notification::getRepository)
                .filter(repoListEntry -> repoListEntry.getValue() != null && !repoListEntry.getValue().isEmpty())
                .sorted((o1, o2) -> Integer.valueOf(o2.getValue().size()).compareTo(o1.getValue().size()))
                .forEach(repoListEntry -> {
                    Repo repo = repoListEntry.getKey();
                    List<Notification> notifications = repoListEntry.getValue();
                    models.add(new GroupedNotificationModel(repo));
                    for (Notification notification : notifications) {
                        models.add(new GroupedNotificationModel(notification));
                    }
                });
        return models;
    }
}
