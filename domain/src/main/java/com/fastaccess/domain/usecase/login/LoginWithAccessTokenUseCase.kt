package com.fastaccess.domain.usecase.login

import com.fastaccess.domain.repository.LoginRemoteRepository
import com.fastaccess.domain.response.UserResponse
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
class LoginWithAccessTokenUseCase @Inject constructor(private val loginRemoteRepository: LoginRemoteRepository) : BaseObservableUseCase<UserResponse>() {
    override fun buildObservable(): Observable<UserResponse> = loginRemoteRepository.loginAccessToken()
}