package com.fastaccess.domain.services

import com.fastaccess.domain.response.body.CommentRequestModel
import com.fastaccess.domain.response.body.DismissReviewRequestModel
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
    ): Observable<Unit>

    @PUT("/repos/{owner}/{repo}/pulls/{number}/reviews/{reviewId}")
    fun editReview(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int,
        @Path("reviewId") reviewId: Long,
        @Body body: CommentRequestModel
    ): Observable<Response<Unit>>

    @PATCH("/repos/{owner}/{repo}/pulls/comments/{id}")
    fun editComment(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("id") id: Long,
        @Body body: CommentRequestModel
    ):  Observable<Response<Unit>>

    @DELETE("repos/{owner}/{repo}/pulls/comments/{id}")
    fun deleteComment(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("id") id: Long
    ): Observable<Response<Unit>>

    @PUT("/repos/{owner}/{repo}/pulls/{number}/reviews/{reviewId}/dismissals")
    fun dismissReview(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int,
        @Path("reviewId") reviewId: Long,
        @Body body: DismissReviewRequestModel
    ): Observable<Response<Unit>>
}