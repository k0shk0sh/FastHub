package com.fastaccess.github.usecase.auth

import com.fastaccess.data.persistence.models.LoginModel
import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.google.gson.Gson
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
class LoginWithAccessTokenUseCase @Inject constructor(private val loginRemoteRepository: LoginRepositoryProvider,
                                                      private val gson: Gson) : BaseObservableUseCase() {
    override fun buildObservable(): Observable<LoginModel> = loginRemoteRepository.loginAccessToken()
            .map { gson.fromJson(gson.toJson(it), LoginModel::class.java) }

    fun insertUser(loginModel: LoginModel): Observable<Boolean> = Observable.fromCallable {
        loginRemoteRepository.logoutAll()
        return@fromCallable loginRemoteRepository.insert(loginModel) > 0
    }
}