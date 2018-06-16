package com.fastaccess.github.di.modules

import com.apollographql.apollo.ApolloClient
import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.data.repository.UserRepositoryProvider
import com.fastaccess.github.di.scopes.PerFragment
import com.fastaccess.github.usecase.auth.GetAccessTokenUseCase
import com.fastaccess.github.usecase.auth.LoginUseCase
import com.fastaccess.github.usecase.auth.LoginWithAccessTokenUseCase
import com.fastaccess.github.usecase.main.IssuesMainScreenUseCase
import com.fastaccess.github.usecase.main.PullRequestsMainScreenUseCase
import com.fastaccess.github.usecase.user.UserUseCase
import com.google.gson.Gson
import dagger.Module
import dagger.Provides

/**
 * Created by Kosh on 12.05.18.
 */
@Module(includes = [FastHubDatabaseModule::class, NetworkModule::class])
class UseCaseModule {

    @PerFragment @Provides fun provideLoginUseCase(loginRemoteRepository: LoginRepositoryProvider): LoginUseCase = LoginUseCase(loginRemoteRepository)

    @PerFragment @Provides fun provideLoginWithAccessTokenUseCase(loginRemoteRepository: LoginRepositoryProvider,
                                                                  gson: Gson): LoginWithAccessTokenUseCase {
        return LoginWithAccessTokenUseCase(loginRemoteRepository, gson)
    }

    @PerFragment @Provides fun provideGetAccessTokenUseCase(loginRemoteRepository: LoginRepositoryProvider): GetAccessTokenUseCase {
        return GetAccessTokenUseCase(loginRemoteRepository)
    }

    @PerFragment @Provides fun provideUserUseCase(userRepository: UserRepositoryProvider): UserUseCase = UserUseCase(userRepository)

    @PerFragment @Provides fun provideIssuesMainScreenUseCase(loginRepository: LoginRepositoryProvider,
                                                              apolloClient: ApolloClient): IssuesMainScreenUseCase {
        return IssuesMainScreenUseCase(loginRepository, apolloClient)
    }

    @PerFragment @Provides fun providePullRequestsMainScreenUseCase(loginRepository: LoginRepositoryProvider,
                                                                    apolloClient: ApolloClient): PullRequestsMainScreenUseCase {
        return PullRequestsMainScreenUseCase(loginRepository, apolloClient)
    }
}