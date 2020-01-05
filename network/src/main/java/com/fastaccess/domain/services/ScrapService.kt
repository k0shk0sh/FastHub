package com.fastaccess.domain.services

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Created by Kosh on 23.01.19.
 */

interface ScrapService {
    @GET fun getTrending(
        @Url url: String,
        @Query("since") since: String?
    ): Observable<String>
}