package com.fastaccess.github.ui.modules.auth.usecase

import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.domain.response.UserResponse
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
class LoginWithAccessTokenUseCase @Inject constructor(private val loginRemoteRepository: LoginRepositoryProvider) : BaseObservableUseCase<UserResponse>() {
    override fun buildObservable(): Observable<UserResponse> = loginRemoteRepository.loginAccessToken()
}