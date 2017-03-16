package com.fastaccess.data.service;


import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.model.Notification;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Kosh on 19 Feb 2017, 6:34 PM
 */

public interface NotificationService {

    @GET("notifications")
    Observable<Pageable<Notification>> getNotifications(@Query("page") int page);

    @GET("notifications?all=true")
    Observable<Pageable<Notification>> getAllNotifications(@Query("page") int page);

    @GET("/notifications/threads/{id}")
    Observable<Notification> getNotification(@Path("id") String id);

    @PATCH("notifications/threads/{id}") Observable<Response<Boolean>> markAsRead(@Path("id") String id);

    @PUT("notifications") Observable<Response<Boolean>> markAllNotificationsAsRead();
}
