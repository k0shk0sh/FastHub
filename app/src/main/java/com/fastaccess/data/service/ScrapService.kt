package com.fastaccess.data.service


import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Kosh on 02 Jun 2017, 12:58 PM
 */

interface ScrapService {

    @GET("{lan}") fun getTrending(@Path("lan") lan: String?, @Query("since") since: String?): Observable<String>

    @GET("{path}") fun getWiki(@Path(value = "path", encoded = true) path: String?): Observable<String>
}
