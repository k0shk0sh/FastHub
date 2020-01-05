package com.fastaccess.github.usecase.auth

import com.fastaccess.data.persistence.models.LoginModel
import com.fastaccess.data.repository.LoginRepository
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.google.gson.Gson
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
class LoginWithAccessTokenUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
    private val gson: Gson,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {
    override fun buildObservable(): Observable<LoginModel> = loginRepository.loginAccessToken()
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
        .map { gson.fromJson(gson.toJson(it), LoginModel::class.java) }

    fun insertUser(loginModel: LoginModel): Observable<LoginModel?> = Observable.fromCallable {
        loginRepository.logoutAll()
        loginRepository.insert(loginModel)
        val login = loginRepository.getLoginBlocking()
        return@fromCallable if (login?.id == loginModel.id) {
            login
        } else {
            null
        }
    }.subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
}