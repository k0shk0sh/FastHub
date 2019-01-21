package com.fastaccess.github.di.modules

import com.apollographql.apollo.ApolloClient
import com.fastaccess.data.repository.*
import com.fastaccess.domain.repository.services.NotificationService
import com.fastaccess.github.di.scopes.PerFragment
import com.fastaccess.github.usecase.auth.GetAccessTokenUseCase
import com.fastaccess.github.usecase.auth.LoginUseCase
import com.fastaccess.github.usecase.auth.LoginWithAccessTokenUseCase
import com.fastaccess.github.usecase.feed.FeedsUseCase
import com.fastaccess.github.usecase.issuesprs.FilterIssuesUseCase
import com.fastaccess.github.usecase.issuesprs.FilterPullRequestsUseCase
import com.fastaccess.github.usecase.main.IssuesMainScreenUseCase
import com.fastaccess.github.usecase.main.PullRequestsMainScreenUseCase
import com.fastaccess.github.usecase.notification.NotificationUseCase
import com.fastaccess.github.usecase.search.FilterSearchReposUseCase
import com.fastaccess.github.usecase.user.BlockUnblockUserUseCase
import com.fastaccess.github.usecase.user.IsUserBlockedUseCase
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

    @PerFragment @Provides fun provideLoginWithAccessTokenUseCase(
        loginRemoteRepository: LoginRepositoryProvider,
        gson: Gson
    ): LoginWithAccessTokenUseCase {
        return LoginWithAccessTokenUseCase(loginRemoteRepository, gson)
    }

    @PerFragment @Provides fun provideGetAccessTokenUseCase(loginRemoteRepository: LoginRepositoryProvider): GetAccessTokenUseCase {
        return GetAccessTokenUseCase(loginRemoteRepository)
    }

    @PerFragment @Provides fun provideUserUseCase(userRepository: UserRepositoryProvider): UserUseCase = UserUseCase(userRepository)

    @PerFragment @Provides fun provideIssuesMainScreenUseCase(
        loginRepository: LoginRepositoryProvider,
        myIssuesPullsRepository: MyIssuesPullsRepositoryProvider,
        apolloClient: ApolloClient
    ): IssuesMainScreenUseCase {
        return IssuesMainScreenUseCase(loginRepository, myIssuesPullsRepository, apolloClient)
    }

    @PerFragment @Provides fun providePullRequestsMainScreenUseCase(
        loginRepository: LoginRepositoryProvider,
        myIssues: MyIssuesPullsRepositoryProvider,
        apolloClient: ApolloClient
    ): PullRequestsMainScreenUseCase {
        return PullRequestsMainScreenUseCase(loginRepository, myIssues, apolloClient)
    }

    @PerFragment @Provides fun provideNotificationUseCase(
        notificationRepositoryProvider: NotificationRepositoryProvider,
        notificationService: NotificationService,
        gson: Gson
    ): NotificationUseCase {
        return NotificationUseCase(notificationRepositoryProvider, notificationService, gson)
    }

    @PerFragment @Provides fun provideFeedsUseCase(provider: FeedsRepositoryProvider): FeedsUseCase = FeedsUseCase(provider)


    @PerFragment @Provides fun provideBlockUnblockUserUseCase(userRepository: UserRepositoryProvider): BlockUnblockUserUseCase {
        return BlockUnblockUserUseCase(userRepository)
    }

    @PerFragment @Provides fun provideIsUserBlockedUseCase(userRepository: UserRepositoryProvider): IsUserBlockedUseCase {
        return IsUserBlockedUseCase(userRepository)
    }

    @PerFragment @Provides fun provideFilterIssuesUseCase(
        loginRepository: LoginRepositoryProvider,
        apolloClient: ApolloClient
    ): FilterIssuesUseCase {
        return FilterIssuesUseCase(loginRepository, apolloClient)
    }

    @PerFragment @Provides fun provideFilterPullRequestsUseCase(
        loginRepository: LoginRepositoryProvider,
        apolloClient: ApolloClient
    ): FilterPullRequestsUseCase {
        return FilterPullRequestsUseCase(loginRepository, apolloClient)
    }

    @PerFragment @Provides fun provideFilterSearchReposUseCase(
        apolloClient: ApolloClient
    ): FilterSearchReposUseCase {
        return FilterSearchReposUseCase(apolloClient)
    }

}