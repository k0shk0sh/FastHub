package com.fastaccess.data.service;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.CommentRequestModel;
import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.ReviewCommentModel;
import com.fastaccess.data.dao.ReviewModel;
import com.fastaccess.data.dao.ReviewRequestModel;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Kosh on 07 May 2017, 1:01 PM
 */

public interface ReviewService {

    @GET("repos/{owner}/{repo}/pulls/{number}/reviews/{id}")
    @Headers("Accept: application/vnd.github.black-cat-preview+json, application/vnd.github.VERSION.html")
    @NonNull
    Observable<ReviewModel> getReview(@Path("owner") String owner, @Path("repo") String repo,
                                      @Path("number") long number, @Path("id") long id);

    @GET("repos/{owner}/{repo}/pulls/{number}/reviews?per_page=100")
    @Headers("Accept: application/vnd.github.black-cat-preview+json, application/vnd.github.VERSION.html")
    @NonNull
    Observable<Pageable<ReviewModel>> getReviews(@Path("owner") String owner, @Path("repo") String repo,
                                                 @Path("number") long number);

    @GET("repos/{owner}/{repo}/pulls/{number}/reviews/{id}/comments")
    @Headers("Accept: application/vnd.github.black-cat-preview+json, application/vnd.github.VERSION.html")
    @NonNull
    Observable<Pageable<ReviewCommentModel>> getReviewComments(@Path("owner") String owner, @Path("repo") String repo,
                                                               @Path("number") long number, @Path("id") long reviewId);

    @GET("repos/{owner}/{repo}/pulls/{number}/comments?per_page=100")
    @Headers("Accept: application/vnd.github.black-cat-preview+json, application/vnd.github.VERSION.html, "
            + "application/vnd.github.squirrel-girl-preview")
    @NonNull
    Observable<Pageable<ReviewCommentModel>> getPrReviewComments(@Path("owner") String owner, @Path("repo") String repo,
                                                                 @Path("number") long number);

    @POST("/repos/{owner}/{repo}/pulls/{number}/comments")
    @Headers("Accept: application/vnd.github.black-cat-preview+json, application/vnd.github.VERSION.html, "
            + "application/vnd.github.squirrel-girl-preview")
    Observable<ReviewCommentModel> submitComment(@Path("owner") String owner, @Path("repo") String repo,
                                                 @Path("number") long number, @Body CommentRequestModel body);

    @PATCH("/repos/{owner}/{repo}/pulls/comments/{id}")
    @Headers("Accept: application/vnd.github.black-cat-preview+json, application/vnd.github.VERSION.html, "
            + "application/vnd.github.squirrel-girl-preview")
    Observable<ReviewCommentModel> editComment(@Path("owner") String owner, @Path("repo") String repo,
                                               @Path("id") long id, @Body CommentRequestModel body);

    @DELETE("repos/{owner}/{repo}/pulls/comments/{id}")
    @Headers("Accept: application/vnd.github.black-cat-preview")
    Observable<Response<Boolean>> deleteComment(@Path("owner") String owner, @Path("repo") String repo, @Path("id") long id);

    @POST("repos/{owner}/{repo}/pulls/{number}/reviews")
    @Headers("Accept: application/vnd.github.black-cat-preview")
    Observable<Response<ReviewModel>> submitPrReview(@Path("owner") String owner, @Path("repo") String repo,
                                                     @Path("number") long number, @NonNull @Body ReviewRequestModel body);
}
