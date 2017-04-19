package com.fastaccess.data.service;


import android.support.annotation.NonNull;

import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.RepoSubscriptionModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Notification;

import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Kosh on 19 Feb 2017, 6:34 PM
 */

public interface NotificationService {

    @GET("notifications") Observable<Pageable<Notification>> getNotifications(@Query("since") String date);

    @GET("notifications?all=true&per_page=200") Observable<Pageable<Notification>> getAllNotifications();

    @PATCH("notifications/threads/{id}") Observable<Response<Boolean>> markAsRead(@Path("id") String id);

    @GET() Observable<Comment> getComment(@Url @NonNull String commentUrl);

    @GET("notifications/threads/{id}/subscription") Observable<RepoSubscriptionModel> isSubscribed(@Path("id") long id);

    @DELETE("notifications/threads/{id}/subscription") Observable<Response<Boolean>> unSubscribe(@Path("id") long id);
}
