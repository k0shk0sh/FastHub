package com.fastaccess.data.service.trending

import com.fastaccess.data.dao.kot.TrendingResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Kosh on 30 May 2017, 11:20 PM
 */

interface TrendingService {
    @GET("v2/trending") fun getTrending(@Query("lan") query: String, @Query("since") since: String): Observable<List<TrendingResponse>>
}