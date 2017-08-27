package com.fastaccess.data.dao;

import android.support.annotation.Nullable;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.data.dao.model.Notification;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.helper.InputHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import static com.annimon.stream.Collectors.toList;

/**
 * Created by Kosh on 18 Apr 2017, 8:07 PM
 */

@Getter @Setter public class GroupedNotificationModel {
    public static final int HEADER = 1;
    public static final int ROW = 2;

    private int type;
    private Repo repo;
    private Notification notification;
    private Date date;

    private GroupedNotificationModel(Repo repo) {
        this.type = HEADER;
        this.repo = repo;
    }

    public GroupedNotificationModel(Notification notification) {
        this.type = ROW;
        this.notification = notification;
        this.date = notification.getUpdatedAt();
    }

    @NonNull public static List<GroupedNotificationModel> construct(@Nullable List<Notification> items) {
        List<GroupedNotificationModel> models = new ArrayList<>();
        if (items == null || items.isEmpty()) return models;
        Map<Repo, List<Notification>> grouped = Stream.of(items)
                .filter(value -> !value.isUnread())
                .collect(Collectors.groupingBy(Notification::getRepository, LinkedHashMap::new,
                        Collectors.mapping(o -> o, toList())));
        Stream.of(grouped)
                .filter(repoListEntry -> repoListEntry.getValue() != null && !repoListEntry.getValue().isEmpty())
                .forEach(repoListEntry -> {
                    Repo repo = repoListEntry.getKey();
                    List<Notification> notifications = repoListEntry.getValue();
                    models.add(new GroupedNotificationModel(repo));
                    Stream.of(notifications)
                            .sorted((o1, o2) -> o2.getUpdatedAt().compareTo(o1.getUpdatedAt()))
                            .forEach(notification -> models.add(new GroupedNotificationModel(notification)));
                });
        return models;
    }

    @NonNull public static List<GroupedNotificationModel> onlyNotifications(@Nullable List<Notification> items) {
        if (items == null || items.isEmpty()) return new ArrayList<>();
        return Stream.of(items)
                .map(GroupedNotificationModel::new)
                .collect(Collectors.toList());
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupedNotificationModel model = (GroupedNotificationModel) o;
        return notification != null && model.getNotification() != null && notification.getId() == (model.notification.getId());
    }

    @Override public int hashCode() {
        return notification != null ? InputHelper.getSafeIntId(notification.getId()) : 0;
    }
}
