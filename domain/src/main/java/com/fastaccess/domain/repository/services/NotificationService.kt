package com.fastaccess.domain.repository.services

import com.fastaccess.domain.response.NotificationResponse
import com.fastaccess.domain.response.PageableResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Kosh on 19.06.18.
 */
interface NotificationService {
    @GET("notifications?per_page=5") fun getMainNotifications(): Observable<PageableResponse<NotificationResponse>>

    @GET("notifications") fun getNotifications(@Query("since") date: String, @Query("page") page: Int):
            Observable<PageableResponse<NotificationResponse>>

    @GET("notifications?all=true&per_page=200") fun getAllNotifications(): Observable<PageableResponse<NotificationResponse>>
}