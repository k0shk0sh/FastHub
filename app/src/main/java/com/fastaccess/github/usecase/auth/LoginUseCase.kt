package com.fastaccess.github.usecase.auth

import com.fastaccess.data.repository.LoginRepository
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.response.AccessTokenResponse
import com.fastaccess.domain.response.AuthBodyModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.github.BuildConfig
import com.fastaccess.github.base.utils.REDIRECT_URL
import com.fastaccess.github.base.utils.SCOPE_LIST
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
class LoginUseCase @Inject constructor(
    private val loginRemoteRepository: LoginRepository,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {
    private var authBodyModel: AuthBodyModel? = null

    override fun buildObservable(): Observable<AccessTokenResponse> = authBodyModel?.let {
        loginRemoteRepository.login(it)
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
    } ?: Observable.empty()

    fun setAuthBody(twoFactorCode: String? = null) {
        this.authBodyModel = AuthBodyModel().apply {
            clientId = com.fastaccess.domain.BuildConfig.GITHUB_CLIENT_ID
            clientSecret = com.fastaccess.domain.BuildConfig.GITHUB_SECRET
            redirectUri = REDIRECT_URL
            scopes = SCOPE_LIST.split(",")
            state = BuildConfig.APPLICATION_ID
            note = BuildConfig.APPLICATION_ID
            noteUrl = "fasthub://login"
            otpCode = twoFactorCode
        }
    }
}