package com.fastaccess.domain.services

import com.fastaccess.domain.response.PostReactionModel
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Kosh on 06.02.19.
 */
interface ReactionService {


    @POST("repos/{repo}/issues/comments/{id}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    fun postIssueCommentReaction(
        @Body body: PostReactionModel,
        @Path("repo") repo: String,
        @Path("id") id: Long
    ): Single<Response<Boolean>>

    @POST("repos/{repo}/issues/{number}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    fun postIssueReaction(
        @Body body: PostReactionModel,
        @Path("repo") repo: String,
        @Path("number") number: Long
    ): Single<Response<Boolean>>

    @POST("repos/{repo}/comments/{id}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    fun postCommitReaction(
        @Body body: PostReactionModel,
        @Path("repo") repo: String,
        @Path("id") id: Long
    ): Single<Response<Boolean>>

    @DELETE("reactions/{id}")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview") fun delete(@Path("id") id: Long): Single<Response<Response<Boolean>>>

    @POST("repos/{repo}/pulls/comments/{id}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview, application/vnd.github.black-cat-preview")
    fun postCommentReviewReaction(
        @Body body: PostReactionModel,
        @Path("repo") repo: String,
        @Path("id") id: Long
    ): Single<Response<Boolean>>
}