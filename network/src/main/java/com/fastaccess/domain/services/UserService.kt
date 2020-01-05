package com.fastaccess.domain.services

import com.fastaccess.domain.response.FeedResponse
import com.fastaccess.domain.response.PageableResponse
import com.fastaccess.domain.response.UserResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*


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

    @PUT("user/following/{username}")
    fun followUser(@Path("username") username: String): Observable<Response<Boolean>>

    @DELETE("user/following/{username}")
    fun unfollowUser(@Path("username") username: String): Observable<Response<Boolean>>

    @GET("user/blocks/{username}")
    @Headers("Accept: application/vnd.github.giant-sentry-fist-preview+json")
    fun isUserBlocked(@Path("username") username: String): Observable<Response<Boolean>>

    @PUT("user/blocks/{username}")
    @Headers("Accept: application/vnd.github.giant-sentry-fist-preview+json")
    fun blockUser(@Path("username") username: String): Observable<Response<Boolean>>

    @DELETE("user/blocks/{username}")
    @Headers("Accept: application/vnd.github.giant-sentry-fist-preview+json")
    fun unBlockUser(@Path("username") username: String): Observable<Response<Boolean>>

}