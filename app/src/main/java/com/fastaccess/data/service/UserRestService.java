package com.fastaccess.data.service;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.AccessTokenModel;
import com.fastaccess.data.dao.EventsModel;
import com.fastaccess.data.dao.LoginModel;
import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.RepoModel;
import com.fastaccess.data.dao.UserModel;

import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Kosh on 08 Feb 2017, 8:54 PM
 */

public interface UserRestService {

    @FormUrlEncoded @POST("access_token")
    Observable<AccessTokenModel> getAccessToken(@NonNull @Field("code") String code,
                                                @NonNull @Field("client_id") String clientId,
                                                @NonNull @Field("client_secret") String clientSecret,
                                                @NonNull @Field("state") String state,
                                                @NonNull @Field("redirect_uri") String redirectUrl);

    @GET("user") Observable<LoginModel> getUser();

    @GET("users/{username}") Observable<UserModel> getUser(@Path("username") @NonNull String username);

    @GET("users/{username}/received_events")
    Observable<Pageable<EventsModel>> getReceivedEvents(@NonNull @Path("username") String userName, @Query("page") int page);

    @GET("users/{username}/repos?sort=pushed&direction=desc")
    Observable<Pageable<RepoModel>> getRepos(@Path("username") @NonNull String username, @Query("page") int page);

    @GET("/user/repos?sort=pushed&direction=desc") Observable<Pageable<RepoModel>> getRepos(@Query("page") int page);

    @GET("users/{username}/starred") Observable<Pageable<RepoModel>>
    getStarred(@Path("username") @NonNull String username, @Query("page") int page);

    @GET("users/{username}/following")
    Observable<Pageable<UserModel>> getFollowing(@Path("username") @NonNull String username, @Query("page") int page);

    @GET("users/{username}/followers")
    Observable<Pageable<UserModel>> getFollowers(@Path("username") @NonNull String username, @Query("page") int page);

    @GET("user/following/{username}")
    Observable<Response<Boolean>> getFollowStatus(@Path("username") @NonNull String username);

    @PUT("/user/following/{username}")
    Observable<Response<Boolean>> followUser(@Path("username") @NonNull String username);

    @DELETE("/user/following/{username}")
    Observable<Response<Boolean>> unfollowUser(@Path("username") @NonNull String username);
}
