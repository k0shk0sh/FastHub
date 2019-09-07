package com.fastaccess.domain.repository.services

import com.fastaccess.domain.response.CommentResponse
import com.fastaccess.domain.response.body.CommentRequestModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface ReviewService {

    @POST("/repos/{owner}/{repo}/pulls/{number}/comments")
    fun submitComment(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Long,
        @Body body: CommentRequestModel
    ): Observable<CommentResponse>

    @PATCH("/repos/{owner}/{repo}/pulls/comments/{id}")
    fun editComment(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("id") id: Long,
        @Body body: CommentRequestModel
    ): Observable<CommentResponse>

    @DELETE("repos/{owner}/{repo}/pulls/comments/{id}")
    fun deleteComment(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("id") id: Long
    ): Observable<Response<Boolean>>
}