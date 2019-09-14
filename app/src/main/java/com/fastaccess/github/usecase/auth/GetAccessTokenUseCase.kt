package com.fastaccess.github.usecase.auth

import com.fastaccess.data.repository.LoginRepository
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.BuildConfig
import com.fastaccess.domain.response.AccessTokenResponse
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.github.base.utils.REDIRECT_URL
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
class GetAccessTokenUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
    private val schedulerProvider: SchedulerProvider
) :
    BaseObservableUseCase() {

    var code: String? = null

    override fun buildObservable(): Observable<AccessTokenResponse> {
        val observable = code?.let {
            loginRepository.getAccessToken(
                it, BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_SECRET,
                com.fastaccess.github.BuildConfig.APPLICATION_ID, REDIRECT_URL
            )
        } ?: Observable.empty()

        return observable
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
    }

}