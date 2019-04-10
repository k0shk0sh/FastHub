package com.fastaccess.github.di.modules

import com.apollographql.apollo.ApolloClient
import com.fastaccess.data.repository.*
import com.fastaccess.domain.repository.services.IssuePrService
import com.fastaccess.domain.repository.services.NotificationService
import com.fastaccess.domain.repository.services.RepoService
import com.fastaccess.github.di.scopes.PerFragment
import com.fastaccess.github.usecase.auth.GetAccessTokenUseCase
import com.fastaccess.github.usecase.auth.LoginUseCase
import com.fastaccess.github.usecase.auth.LoginWithAccessTokenUseCase
import com.fastaccess.github.usecase.feed.FeedsUseCase
import com.fastaccess.github.usecase.issuesprs.*
import com.fastaccess.github.usecase.main.IssuesMainScreenUseCase
import com.fastaccess.github.usecase.main.PullRequestsMainScreenUseCase
import com.fastaccess.github.usecase.notification.NotificationUseCase
import com.fastaccess.github.usecase.search.FilterSearchReposUseCase
import com.fastaccess.github.usecase.search.FilterSearchUsersUseCase
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

    @PerFragment @Provides fun provideFilterSearchUsersUseCase(
        apolloClient: ApolloClient
    ): FilterSearchUsersUseCase {
        return FilterSearchUsersUseCase(apolloClient)
    }

    @PerFragment @Provides fun provideGetIssueUseCase(
        issueRepositoryProvider: IssueRepositoryProvider,
        apolloClient: ApolloClient
    ): GetIssueUseCase {
        return GetIssueUseCase(issueRepositoryProvider, apolloClient)
    }

    @PerFragment @Provides fun provideGetIssueTimelineUseCase(
        issueRepositoryProvider: IssueRepositoryProvider,
        apolloClient: ApolloClient
    ): GetIssueTimelineUseCase {
        return GetIssueTimelineUseCase(issueRepositoryProvider, apolloClient)
    }

    @PerFragment @Provides fun provideEditIssuePrUseCase(
        issueRepositoryProvider: IssueRepositoryProvider,
        issuePrService: IssuePrService,
        loginRepositoryProvider: LoginRepositoryProvider
    ): CloseOpenIssuePrUseCase {
        return CloseOpenIssuePrUseCase(issueRepositoryProvider, issuePrService, loginRepositoryProvider)
    }

    @PerFragment @Provides fun provideLockUnlockIssuePrUseCase(
        issueRepositoryProvider: IssueRepositoryProvider,
        apolloClient: ApolloClient,
        loginRepositoryProvider: LoginRepositoryProvider
    ): LockUnlockIssuePrUseCase {
        return LockUnlockIssuePrUseCase(issueRepositoryProvider, apolloClient, loginRepositoryProvider)
    }

    @PerFragment @Provides fun provideGetLabelsUseCase(
        apolloClient: ApolloClient
    ): GetLabelsUseCase {
        return GetLabelsUseCase(apolloClient)
    }

    @PerFragment @Provides fun provideCreateLabelUseCase(
        repoService: RepoService
    ): CreateLabelUseCase {
        return CreateLabelUseCase(repoService)
    }

    @PerFragment @Provides fun providePutLabelsUseCase(
        repoService: RepoService
    ): PutLabelsUseCase {
        return PutLabelsUseCase(repoService)
    }

    @PerFragment @Provides fun provideGetAssigneesUseCase(
        apolloClient: ApolloClient
    ): GetAssigneesUseCase {
        return GetAssigneesUseCase(apolloClient)
    }

    @PerFragment @Provides fun provideAddAssigneesUseCase(
        repoService: RepoService
    ): AddAssigneesUseCase {
        return AddAssigneesUseCase(repoService)
    }

    @PerFragment @Provides fun provideGetMilestonesUseCase(
        apolloClient: ApolloClient
    ): GetMilestonesUseCase {
        return GetMilestonesUseCase(apolloClient)
    }

    @PerFragment @Provides fun provideCreateMilestoneUseCase(
        repoService: RepoService
    ): CreateMilestoneUseCase {
        return CreateMilestoneUseCase(repoService)
    }

    @PerFragment @Provides fun provideMilestoneIssuePrUseCase(
        issueRepositoryProvider: IssueRepositoryProvider,
        issuePrService: IssuePrService,
        loginRepositoryProvider: LoginRepositoryProvider
    ): MilestoneIssuePrUseCase {
        return MilestoneIssuePrUseCase(issueRepositoryProvider, issuePrService, loginRepositoryProvider)
    }
}