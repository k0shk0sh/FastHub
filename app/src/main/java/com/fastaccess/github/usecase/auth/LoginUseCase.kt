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
        this.authBodyModel = AuthBodyModel(BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_SECRET,
                "fasthub://login", arrayListOf("user", "repo", "gist", "notifications", "read:org"),
                BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID, "fasthub://login", twoFactorCode)
    }
}