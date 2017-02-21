package com.fastaccess.data.service;

import com.fastaccess.data.dao.NotificationThreadModel;
import com.fastaccess.data.dao.Pageable;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Kosh on 19 Feb 2017, 6:34 PM
 */

public interface NotificationService {

    @GET("notifications")
    Observable<Pageable<NotificationThreadModel>> getNotifications();

    @GET("/notifications/threads/{id}")
    Observable<NotificationThreadModel> getNotification(@Path("id") String id);

    @PATCH("notifications/threads/{id}") Observable<Response<Boolean>> markAsRead(@Path("id") String id);
}
