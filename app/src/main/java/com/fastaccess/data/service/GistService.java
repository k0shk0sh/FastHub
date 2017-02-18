package com.fastaccess.data.service;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.CommentRequestModel;
import com.fastaccess.data.dao.CommentsModel;
import com.fastaccess.data.dao.CreateGistModel;
import com.fastaccess.data.dao.GistsModel;
import com.fastaccess.data.dao.Pageable;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Kosh on 20 Nov 2016, 10:28 AM
 */

public interface GistService {

    @POST("gists") Observable<GistsModel> createGist(@Body CreateGistModel gistBody);

    @POST("gists/{id}") Observable<GistsModel> editGist(@Body CreateGistModel gistBody);

    @DELETE("gists/{id}") Observable<Response<Boolean>> deleteGist(@Path("id") String id);

    @GET("gists/public") Observable<Pageable<GistsModel>> getPublicGists(@Query("per_page") int perPage, @Query("page") int page);

    @GET("gists") Observable<Pageable<GistsModel>> getMyGists(@Query("per_page") int perPage, @Query("page") int page);

    @GET("users/{username}/gists")
    Observable<Pageable<GistsModel>> getUserGists(@NonNull @Path("username") String username,
                                                  @Query("per_page") int perPage, @Query("page") int page);

    @GET("gists/{id}")
    Observable<GistsModel> getGist(@Path("id") String id);

    @GET("gists/{id}/comments")
    Observable<Pageable<CommentsModel>> getGistComments(@NonNull @Path("id") String id, @Query("page") int page);

    @GET("gists/{gist_id}/comments/{id}")
    Observable<CommentsModel> getGistComment(@Path("gist_id") String gistId, @Path("id") String id);

    @POST("gists/{gist_id}/comments")
    Observable<CommentsModel> createGistComment(@Path("gist_id") String gistId, @Body CommentRequestModel body);

    @PATCH("gists/{gist_id}/comments/{id}")
    Observable<CommentsModel> editGistComment(@Path("gist_id") String gistId, @Path("id") long id, @Body CommentRequestModel body);

    @DELETE("gists/{gist_id}/comments/{id}")
    Observable<Response<Boolean>> deleteGistComment(@Path("gist_id") String gistId, @Path("id") long id);

    @GET("gists/{gist_id}/star")
    Observable<Response<Boolean>> checkGistStar(@Path("gist_id") @NonNull String gistId);

    @PUT("gists/{gist_id}/star")
    Observable<Response<Boolean>> starGist(@Path("gist_id") @NonNull String gistId);

    @DELETE("gists/{gist_id}/star")
    Observable<Response<Boolean>> unStarGist(@Path("gist_id") @NonNull String gistId);

    @POST("gists/{gist_id}/forks")
    Observable<Response<GistsModel>> forkGist(@Path("gist_id") @NonNull String gistId);

}
