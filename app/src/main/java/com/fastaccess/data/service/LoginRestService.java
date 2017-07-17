package com.fastaccess.data.service;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.AccessTokenModel;
import com.fastaccess.data.dao.AuthModel;
import com.fastaccess.data.dao.model.Login;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LoginRestService {

    @GET("user") Observable<Login> loginAccessToken();

    @POST("authorizations") Observable<AccessTokenModel> login(@NonNull @Body AuthModel authModel);

    @FormUrlEncoded @POST("access_token")
    @Headers("Accept: application/json")
    Observable<AccessTokenModel> getAccessToken(@NonNull @Field("code") String code,
                                                @NonNull @Field("client_id") String clientId,
                                                @NonNull @Field("client_secret") String clientSecret,
                                                @NonNull @Field("state") String state,
                                                @NonNull @Field("redirect_uri") String redirectUrl);
}
