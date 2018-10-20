package com.fastaccess.domain.repository.services

import com.fastaccess.domain.response.FeedResponse
import com.fastaccess.domain.response.PageableResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * Created by Kosh on 20.10.18.
 */
interface OrganizationService {

    @GET("users/{username}/events/orgs/{org}")
    fun getReceivedEvents(
            @Path("username") userName: String,
            @Path("org") org: String,
            @Query("page") page: Int
    ): Observable<PageableResponse<FeedResponse>>

}