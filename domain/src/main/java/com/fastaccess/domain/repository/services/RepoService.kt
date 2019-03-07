package com.fastaccess.domain.repository.services

import com.fastaccess.domain.response.LabelResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by Kosh on 07.03.19.
 */
interface RepoService {
    @POST("repos/{owner}/{repo}/labels")
    fun addLabel(@Path("owner") owner: String, @Path("repo") repo: String, @Body body: LabelResponse): Observable<LabelResponse>
}