package com.fastaccess.github.usecase.auth

import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.domain.response.AccessTokenResponse
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.github.BuildConfig
import com.fastaccess.github.utils.REDIRECT_URL
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
class GetAccessTokenUseCase @Inject constructor(private val loginRemoteRepository: LoginRepositoryProvider) :
        BaseObservableUseCase() {

    var code: String? = null

    override fun buildObservable(): Observable<AccessTokenResponse> = code?.let {
        loginRemoteRepository.getAccessToken(it, BuildConfig.GITHUB_CLIENT_ID, BuildConfig.GITHUB_SECRET,
                BuildConfig.APPLICATION_ID, REDIRECT_URL)
    } ?: Observable.empty()

}