package com.fastaccess.domain.repository.services

import com.fastaccess.domain.response.FeedResponse
import com.fastaccess.domain.response.PageableResponse
import com.fastaccess.domain.response.UserResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Kosh on 03.06.18.
 */
interface UserService {
    @GET("user") fun getUser(): Observable<UserResponse>

    @GET("users/{username}/received_events?per_page=5")
    fun getMainScreenReceivedEvents(@Path("username") userName: String): Observable<PageableResponse<FeedResponse>>

    @GET("users/{username}/received_events?per_page=30")
    fun getReceivedEvents(@Path("username") userName: String, @Query("page") page: Int): Observable<PageableResponse<FeedResponse>>

    @GET("users/{username}/events")
    fun getUserEvents(@Path("username") userName: String, @Query("page") page: Int): Observable<PageableResponse<FeedResponse>>

}