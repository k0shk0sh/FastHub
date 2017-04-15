package com.fastaccess.data.service;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.AccessTokenModel;
import com.fastaccess.data.dao.AuthModel;
import com.fastaccess.data.dao.model.Login;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

public interface LoginRestService {

    @POST("authorizations") Observable<AccessTokenModel> login(@NonNull @Body AuthModel authModel);

    @FormUrlEncoded @POST("access_token")
    @Headers("Accept: application/json")
    Observable<AccessTokenModel> getAccessToken(@NonNull @Field("code") String code,
                                                @NonNull @Field("client_id") String clientId,
                                                @NonNull @Field("client_secret") String clientSecret,
                                                @NonNull @Field("state") String state,
                                                @NonNull @Field("redirect_uri") String redirectUrl);

    @POST("user") @Headers("Accept: application/json") Observable<Login> loginAndGetUser(@NonNull @Body AuthModel authModel);
}
