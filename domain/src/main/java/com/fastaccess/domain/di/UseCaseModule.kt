package com.fastaccess.domain.di

import com.fastaccess.domain.di.scopes.PerFragment
import com.fastaccess.domain.repository.LoginRemoteRepository
import com.fastaccess.domain.usecase.login.GetAccessTokenUseCase
import com.fastaccess.domain.usecase.login.LoginUseCase
import com.fastaccess.domain.usecase.login.LoginWithAccessTokenUseCase
import dagger.Module
import dagger.Provides

/**
 * Created by Kosh on 12.05.18.
 */
@Module
abstract class UseCaseModule(private val clientId: String,
                    private val clientSecret: String,
                    private val state: String,
                    private val redirectUrl: String) {

    @PerFragment @Provides fun provideLoginUseCase(loginRemoteRepository: LoginRemoteRepository): LoginUseCase = LoginUseCase(loginRemoteRepository)

    @PerFragment @Provides fun provideLoginWithAccessTokenUseCase(loginRemoteRepository: LoginRemoteRepository): LoginWithAccessTokenUseCase {
        return LoginWithAccessTokenUseCase(loginRemoteRepository)
    }

    @PerFragment @Provides fun provideGetAccessTokenUseCase(loginRemoteRepository: LoginRemoteRepository): GetAccessTokenUseCase {
        return GetAccessTokenUseCase(loginRemoteRepository, clientId, clientSecret, state, redirectUrl)
    }

}