package com.fastaccess.domain.repository.services

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Kosh on 23.01.19.
 */

interface ScrapService {
    @GET("{lan}") fun getTrending(@Path("lan") lan: String?, @Query("since") since: String?): Observable<String>
}