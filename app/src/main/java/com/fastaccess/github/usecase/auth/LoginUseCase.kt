package com.fastaccess.github.usecase.auth

import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.domain.response.AccessTokenResponse
import com.fastaccess.domain.response.AuthBodyModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.github.BuildConfig
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
class LoginUseCase @Inject constructor(private val loginRemoteRepository: LoginRepositoryProvider) : BaseObservableUseCase() {
    private var authBodyModel: AuthBodyModel? = null

    override fun buildObservable(): Observable<AccessTokenResponse> = authBodyModel?.let { loginRemoteRepository.login(it) } ?: Observable.empty()

    fun setAuthBody(twoFactorCode: String? = null) {
        this.authBodyModel = AuthBodyModel().apply {
            clientId = BuildConfig.GITHUB_CLIENT_ID
            clientSecret = BuildConfig.GITHUB_SECRET
            redirectUri = "fasthub://login"
            scopes = listOf("user", "repo", "gist", "notifications", "read:org")
            state = BuildConfig.APPLICATION_ID
            note = BuildConfig.APPLICATION_ID
            noteUrl = "fasthub://login"
            otpCode = twoFactorCode
        }
    }
}