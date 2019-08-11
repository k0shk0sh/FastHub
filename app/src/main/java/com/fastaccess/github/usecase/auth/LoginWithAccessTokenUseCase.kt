package com.fastaccess.github.usecase.auth

import com.fastaccess.data.persistence.models.LoginModel
import com.fastaccess.data.repository.LoginLocalRepository
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.repository.LoginRemoteRepository
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.google.gson.Gson
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
class LoginWithAccessTokenUseCase @Inject constructor(
    private val loginRemoteRepository: LoginRemoteRepository,
    private val loginLocalRepository: LoginLocalRepository,
    private val gson: Gson,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {
    override fun buildObservable(): Observable<LoginModel> = loginRemoteRepository.loginAccessToken()
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
        .map { gson.fromJson(gson.toJson(it), LoginModel::class.java) }

    fun insertUser(loginModel: LoginModel): Observable<LoginModel?> = Observable.fromCallable {
        loginLocalRepository.logoutAll()
        loginLocalRepository.insert(loginModel)
        val login = loginLocalRepository.getLoginBlocking()
        return@fromCallable if (login?.id == loginModel.id) {
            login
        } else {
            null
        }
    }.subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
}