package com.fastaccess.data.service;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.CommentRequestModel;
import com.fastaccess.data.dao.CreateGistModel;
import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Gist;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Kosh on 20 Nov 2016, 10:28 AM
 */

public interface GistService {

    @NonNull @GET @Headers("Accept: application/vnd.github.VERSION.raw")
    Observable<String> getGistFile(@Url String url);

    @POST("gists") Observable<Gist> createGist(@Body CreateGistModel gistBody);

    @PATCH("gists/{id}") Observable<Gist> editGist(@Body CreateGistModel gistBody, @Path("id") String id);

    @DELETE("gists/{id}") Observable<Response<Boolean>> deleteGist(@Path("id") String id);

    @GET("gists/public") Observable<Pageable<Gist>> getPublicGists(@Query("per_page") int perPage, @Query("page") int page);

    @GET("gists") Observable<Pageable<Gist>> getMyGists(@Query("page") int page);

    @GET("users/{username}/gists")
    Observable<Pageable<Gist>> getUserGists(@NonNull @Path("username") String username, @Query("page") int page);

    @GET("gists/{id}")
    Observable<Gist> getGist(@Path("id") String id);

    @GET("gists/{id}/comments")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    Observable<Pageable<Comment>> getGistComments(@NonNull @Path("id") String id, @Query("page") int page);

    @POST("gists/{gist_id}/comments")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    Observable<Comment> createGistComment(@Path("gist_id") String gistId, @Body CommentRequestModel body);

    @PATCH("gists/{gist_id}/comments/{id}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    Observable<Comment> editGistComment(@Path("gist_id") String gistId, @Path("id") long id, @Body CommentRequestModel body);

    @DELETE("gists/{gist_id}/comments/{id}")
    Observable<Response<Boolean>> deleteGistComment(@Path("gist_id") String gistId, @Path("id") long id);

    @GET("gists/{gist_id}/star")
    Observable<Response<Boolean>> checkGistStar(@Path("gist_id") @NonNull String gistId);

    @PUT("gists/{gist_id}/star")
    Observable<Response<Boolean>> starGist(@Path("gist_id") @NonNull String gistId);

    @DELETE("gists/{gist_id}/star")
    Observable<Response<Boolean>> unStarGist(@Path("gist_id") @NonNull String gistId);

    @POST("gists/{gist_id}/forks")
    Observable<Response<Gist>> forkGist(@Path("gist_id") @NonNull String gistId);

    @GET("/gists/starred") Observable<Pageable<Gist>> getStarredGists(@Query("page") int page);

}
