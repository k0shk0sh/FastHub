package com.fastaccess.domain.usecase.login

import com.fastaccess.domain.repository.LoginRemoteRepository
import com.fastaccess.domain.response.AccessTokenResponse
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
class GetAccessTokenUseCase @Inject constructor(private val loginRemoteRepository: LoginRemoteRepository,
                                                private val clientId: String,
                                                private val clientSecret: String,
                                                private val state: String,
                                                private val redirectUrl: String) : BaseObservableUseCase<AccessTokenResponse>() {

    var code: String? = null

    override fun buildObservable(): Observable<AccessTokenResponse> = code?.let {
        loginRemoteRepository.getAccessToken(it, clientId, clientSecret, state, redirectUrl)
    } ?: Observable.empty()

}