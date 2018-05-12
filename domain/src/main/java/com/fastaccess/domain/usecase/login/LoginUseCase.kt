package com.fastaccess.domain.usecase.login

import com.fastaccess.domain.repository.LoginRemoteRepository
import com.fastaccess.domain.response.AccessTokenResponse
import com.fastaccess.domain.response.AuthBodyModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
class LoginUseCase @Inject constructor(private val loginRemoteRepository: LoginRemoteRepository) : BaseObservableUseCase<AccessTokenResponse>() {
    var authBodyModel: AuthBodyModel? = null
    override fun buildObservable(): Observable<AccessTokenResponse> = authBodyModel?.let { loginRemoteRepository.login(it) } ?: Observable.empty()
}