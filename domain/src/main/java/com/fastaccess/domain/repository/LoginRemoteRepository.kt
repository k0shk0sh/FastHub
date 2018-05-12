package com.fastaccess.domain.repository

import com.fastaccess.domain.response.AccessTokenResponse
import com.fastaccess.domain.response.AuthBodyModel
import com.fastaccess.domain.response.UserResponse
import io.reactivex.Observable

/**
 * Created by Kosh on 12.05.18.
 */
interface LoginRemoteRepository {
    fun loginAccessToken(): Observable<UserResponse>
    fun login(authModel: AuthBodyModel): Observable<AccessTokenResponse>
    fun getAccessToken(code: String,
                       clientId: String,
                       clientSecret: String,
                       state: String,
                       redirectUrl: String): Observable<AccessTokenResponse>
}