package com.fastaccess.github.di.modules

import com.fastaccess.data.repository.LoginLocalRepositoryProvider
import com.fastaccess.github.di.scopes.PerFragment
import com.fastaccess.github.ui.modules.auth.usecase.GetAccessTokenUseCase
import com.fastaccess.github.ui.modules.auth.usecase.LoginUseCase
import com.fastaccess.github.ui.modules.auth.usecase.LoginWithAccessTokenUseCase
import dagger.Module
import dagger.Provides

/**
 * Created by Kosh on 12.05.18.
 */
@Module(includes = [FastHubDatabaseModule::class, NetworkModule::class])
class UseCaseModule {

    @PerFragment @Provides fun provideLoginUseCase(
            loginRemoteRepository: LoginLocalRepositoryProvider): LoginUseCase = LoginUseCase(loginRemoteRepository)

    @PerFragment @Provides fun provideLoginWithAccessTokenUseCase(loginRemoteRepository: LoginLocalRepositoryProvider): LoginWithAccessTokenUseCase {
        return LoginWithAccessTokenUseCase(loginRemoteRepository)
    }

    @PerFragment @Provides fun provideGetAccessTokenUseCase(loginRemoteRepository: LoginLocalRepositoryProvider): GetAccessTokenUseCase {
        return GetAccessTokenUseCase(loginRemoteRepository)
    }

}