package com.fastaccess.data.service;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.PostReactionModel;
import com.fastaccess.data.dao.ReactionsModel;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import io.reactivex.Observable;

/**
 * Created by Kosh on 29 Mar 2017, 9:48 PM
 */
public interface ReactionsService {

    @POST("repos/{owner}/{repo}/issues/comments/{id}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    Observable<ReactionsModel> postIssueCommentReaction(@NonNull @Body PostReactionModel body,
                                                        @NonNull @Path("owner") String owner,
                                                        @Path("repo") @NonNull String repo,
                                                        @Path("id") long id);

    @POST("repos/{owner}/{repo}/issues/{number}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    Observable<ReactionsModel> postIssueReaction(@NonNull @Body PostReactionModel body,
                                                 @NonNull @Path("owner") String owner,
                                                 @Path("repo") @NonNull String repo,
                                                 @Path("number") long number);

    @POST("repos/{owner}/{repo}/comments/{id}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    Observable<ReactionsModel> postCommitReaction(@NonNull @Body PostReactionModel body,
                                                  @NonNull @Path("owner") String owner,
                                                  @Path("repo") @NonNull String repo,
                                                  @Path("id") long id);

    @DELETE("reactions/{id}")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    Observable<Response<Boolean>> delete(@Path("id") long id);

    @GET("repos/{owner}/{repo}/issues/comments/{id}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    Observable<Pageable<ReactionsModel>> getIssueCommentReaction(@NonNull @Path("owner") String owner,
                                                                 @Path("repo") @NonNull String repo,
                                                                 @Path("id") long id,
                                                                 @Query("content") @NonNull String content,
                                                                 @Query("page") int page);

    @GET("repos/{owner}/{repo}/issues/{number}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    Observable<Pageable<ReactionsModel>> getIssueReaction(@NonNull @Path("owner") String owner,
                                                          @Path("repo") @NonNull String repo,
                                                          @Path("number") long id,
                                                          @Query("content") @NonNull String content,
                                                          @Query("page") int page);

    @GET("repos/{owner}/{repo}/comments/{id}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    Observable<Pageable<ReactionsModel>> getCommitReaction(@NonNull @Path("owner") String owner,
                                                           @Path("repo") @NonNull String repo,
                                                           @Path("id") long id,
                                                           @Query("content") @NonNull String content,
                                                           @Query("page") int page);

    @GET("repos/{owner}/{repo}/pulls/comments/{id}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview, application/vnd.github.black-cat-preview")
    Observable<Pageable<ReactionsModel>> getPullRequestReactions(@NonNull @Path("owner") String owner,
                                                                 @Path("repo") @NonNull String repo,
                                                                 @Path("id") long id,
                                                                 @Query("content") @NonNull String content,
                                                                 @Query("page") int page);

    @POST("repos/{owner}/{repo}/pulls/comments/{id}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview, application/vnd.github.black-cat-preview")
    Observable<ReactionsModel> postCommentReviewReaction(@NonNull @Body PostReactionModel body,
                                                         @NonNull @Path("owner") String owner,
                                                         @Path("repo") @NonNull String repo,
                                                         @Path("id") long id);
}

