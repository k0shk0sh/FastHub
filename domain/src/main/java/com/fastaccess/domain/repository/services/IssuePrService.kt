package com.fastaccess.domain.repository.services

import com.fastaccess.domain.response.IssueRequestModel
import com.fastaccess.domain.response.IssueResponse
import com.fastaccess.domain.response.body.CommentRequestModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*


/**
 * Created by Kosh on 16.02.19.
 */
interface IssuePrService {

    @PATCH("repos/{owner}/{repo}/issues/{number}")
    fun editIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int,
        @Body issue: IssueRequestModel
    ): Observable<IssueResponse>

    @POST("repos/{owner}/{repo}/issues/{number}/comments")
    fun createIssueComment(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int,
        @Body body: CommentRequestModel
    ):  Observable<Response<Void>>

    @PATCH("repos/{owner}/{repo}/issues/comments/{id}")
    fun editIssueComment(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("id") id: Long,
        @Body body: CommentRequestModel
    ): Observable<Response<Void>>

    @DELETE("repos/{owner}/{repo}/issues/comments/{id}")
    fun deleteIssueComment(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("id") id: Long
    ): Observable<Response<Void>>
}