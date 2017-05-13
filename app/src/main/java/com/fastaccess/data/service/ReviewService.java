package com.fastaccess.data.service;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.ReviewCommentModel;
import com.fastaccess.data.dao.ReviewModel;

import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Kosh on 07 May 2017, 1:01 PM
 */

public interface ReviewService {

    @GET("repos/{owner}/{repo}/pulls/{number}/reviews/{id}")
    @Headers("Accept: application/vnd.github.black-cat-preview+json, application/vnd.github.VERSION.html")
    @NonNull
    Observable<ReviewModel> getReview(@Path("owner") String owner, @Path("repo") String repo,
                                      @Path("number") long number, @Path("id") long id);

    @GET("repos/{owner}/{repo}/pulls/{number}/reviews?per_page=200")
    @Headers("Accept: application/vnd.github.black-cat-preview+json, application/vnd.github.VERSION.html")
    @NonNull
    Observable<Pageable<ReviewModel>> getReviews(@Path("owner") String owner, @Path("repo") String repo,
                                                 @Path("number") long number);

    @GET("repos/{owner}/{repo}/pulls/{number}/reviews/{id}/comments")
    @Headers("Accept: application/vnd.github.black-cat-preview+json, application/vnd.github.VERSION.html")
    @NonNull
    Observable<Pageable<ReviewCommentModel>> getReviewComments(@Path("owner") String owner, @Path("repo") String repo,
                                                               @Path("number") long number, @Path("id") long reviewId);

    @GET("repos/{owner}/{repo}/pulls/{number}/comments")
    @Headers("Accept: application/vnd.github.black-cat-preview+json, application/vnd.github.VERSION.html, "
            + "application/vnd.github.squirrel-girl-preview")
    @NonNull
    Observable<Pageable<ReviewCommentModel>> getPrReviewComments(@Path("owner") String owner, @Path("repo") String repo,
                                                                 @Path("number") long number);

    @DELETE("repos/{owner}/{repo}/pulls/{number}/comments/{id}")
    @Headers("Accept: application/vnd.github.black-cat-preview")
    Observable<Response<Boolean>> deleteComment(@Path("owner") String owner, @Path("repo") String repo,
                                                @Path("number") long number, @Path("id") long id);
}
