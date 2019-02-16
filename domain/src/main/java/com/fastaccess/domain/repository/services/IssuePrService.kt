package com.fastaccess.domain.repository.services

import com.fastaccess.domain.response.IssueRequestModel
import com.fastaccess.domain.response.IssueResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.Path


/**
 * Created by Kosh on 16.02.19.
 */
interface IssuePrService {

    @PATCH("repos/{owner}/{repo}/issues/{number}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    fun editIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int,
        @Body issue: IssueRequestModel
    ): Observable<IssueResponse>
}