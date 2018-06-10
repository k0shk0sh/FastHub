package com.fastaccess.data.repository.services

import com.fastaccess.data.persistence.models.UserModel
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Created by Kosh on 03.06.18.
 */
interface UserService {
    @GET("user") fun getUser(): Observable<UserModel>
}