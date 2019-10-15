package com.fastaccess.domain.services

import com.fastaccess.domain.response.FileResponseModel
import com.fastaccess.domain.response.PageableResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PullRequestService {
    @GET("repos/{owner}/{repo}/pulls/{number}/files") fun getPullRequestFiles(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int,
        @Query("page") page: Int
    ): Observable<PageableResponse<FileResponseModel>>
}