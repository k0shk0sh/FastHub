package com.fastaccess.github.ui.modules.auth.usecase

import com.fastaccess.data.repository.LoginLocalRepositoryProvider
import com.fastaccess.domain.response.AccessTokenResponse
import com.fastaccess.domain.response.AuthBodyModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
class LoginUseCase @Inject constructor(private val loginRemoteRepository: LoginLocalRepositoryProvider) : BaseObservableUseCase<AccessTokenResponse>() {
    var authBodyModel: AuthBodyModel? = null

    override fun buildObservable(): Observable<AccessTokenResponse> = authBodyModel?.let { loginRemoteRepository.login(it) } ?: Observable.empty()
}