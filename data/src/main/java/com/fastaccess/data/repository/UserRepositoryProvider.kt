package com.fastaccess.data.repository

import com.fastaccess.data.persistence.models.UserModel
import com.fastaccess.data.repository.services.UserService
import com.fastaccess.domain.repository.UserRemoteRepository
import com.google.gson.Gson
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 10.06.18.
 */

class UserRepositoryProvider @Inject constructor(private val userService: UserService,
                                                 private val gson: Gson) : UserRemoteRepository {
    override fun getUser(): Observable<UserModel> = userService.getUser().map { gson.fromJson(gson.toJson(it), UserModel::class.java) }
}