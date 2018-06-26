package com.fastaccess.domain.repository.services

import com.fastaccess.domain.response.AccessTokenResponse
import com.fastaccess.domain.response.AuthBodyModel
import com.fastaccess.domain.response.UserResponse
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by Kosh on 11.05.18.
 */
interface LoginService {
    @GET("user") fun loginAccessToken(): Observable<UserResponse>

    @POST("authorizations") fun login(@Body authModel: AuthBodyModel): Observable<AccessTokenResponse>

    @FormUrlEncoded @POST("access_token") @Headers("Accept: application/json")
    fun getAccessToken(@Field("code") code: String,
                       @Field("client_id") clientId: String,
                       @Field("client_secret") clientSecret: String,
                       @Field("state") state: String,
                       @Field("redirect_uri") redirectUrl: String): Observable<AccessTokenResponse>
}