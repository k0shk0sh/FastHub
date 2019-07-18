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
        gson: Gson,
        schedulerProvider: SchedulerProvider
    ): LoginWithAccessTokenUseCase {
        return LoginWithAccessTokenUseCase(loginRemoteRepository, gson, schedulerProvider)
    }

    @PerFragment @Provides fun provideGetAccessTokenUseCase(
        loginRemoteRepository: LoginRepositoryProvider,
        schedulerProvider: SchedulerProvider
    ): GetAccessTokenUseCase {
        return GetAccessTokenUseCase(loginRemoteRepository, schedulerProvider)
    }

    @PerFragment @Provides fun provideUserUseCase(
        userRepository: UserRepositoryProvider,
        schedulerProvider: SchedulerProvider
    ): UserUseCase = UserUseCase(userRepository, schedulerProvider)

    @PerFragment @Provides fun provideIssuesMainScreenUseCase(
        loginRepository: LoginRepositoryProvider,
        myIssuesPullsRepository: MyIssuesPullsRepositoryProvider,
        apolloClient: ApolloClient,
        schedulerProvider: SchedulerProvider
    ): IssuesMainScreenUseCase {
        return IssuesMainScreenUseCase(loginRepository, myIssuesPullsRepository, apolloClient, schedulerProvider)
    }

    @PerFragment @Provides fun providePullRequestsMainScreenUseCase(
        loginRepository: LoginRepositoryProvider,
        myIssues: MyIssuesPullsRepositoryProvider,
        apolloClient: ApolloClient,
        schedulerProvider: SchedulerProvider
    ): PullRequestsMainScreenUseCase {
        return PullRequestsMainScreenUseCase(loginRepository, myIssues, apolloClient, schedulerProvider)
    }

    @PerFragment @Provides fun provideNotificationUseCase(
        notificationRepositoryProvider: NotificationRepositoryProvider,
        notificationService: NotificationService,
        gson: Gson,
        schedulerProvider: SchedulerProvider
    ): NotificationUseCase {
        return NotificationUseCase(notificationRepositoryProvider, notificationService, gson, schedulerProvider)
    }

    @PerFragment @Provides fun provideFeedsUseCase(
        provider: FeedsRepositoryProvider,
        schedulerProvider: SchedulerProvider
    ): FeedsUseCase = FeedsUseCase(provider, schedulerProvider)


    @PerFragment @Provides fun provideBlockUnblockUserUseCase(
        userRepository: UserRepositoryProvider,
        schedulerProvider: SchedulerProvider
    ): BlockUnblockUserUseCase {
        return BlockUnblockUserUseCase(userRepository, schedulerProvider)
    }

    @PerFragment @Provides fun provideIsUserBlockedUseCase(
        userRepository: UserRepositoryProvider,
        schedulerProvider: SchedulerProvider
    ): IsUserBlockedUseCase {
        return IsUserBlockedUseCase(userRepository, schedulerProvider)
    }

    @PerFragment @Provides fun provideFilterIssuesUseCase(
        loginRepository: LoginRepositoryProvider,
        apolloClient: ApolloClient,
        schedulerProvider: SchedulerProvider
    ): FilterIssuesUseCase {
        return FilterIssuesUseCase(loginRepository, apolloClient, schedulerProvider)
    }

    @PerFragment @Provides fun provideFilterPullRequestsUseCase(
        loginRepository: LoginRepositoryProvider,
        apolloClient: ApolloClient,
        schedulerProvider: SchedulerProvider
    ): FilterPullRequestsUseCase {
        return FilterPullRequestsUseCase(loginRepository, apolloClient, schedulerProvider)
    }

    @PerFragment @Provides fun provideFilterSearchReposUseCase(
        apolloClient: ApolloClient,
        schedulerProvider: SchedulerProvider
    ): FilterSearchReposUseCase {
        return FilterSearchReposUseCase(apolloClient, schedulerProvider)
    }

    @PerFragment @Provides fun provideFilterSearchUsersUseCase(
        apolloClient: ApolloClient,
        schedulerProvider: SchedulerProvider
    ): FilterSearchUsersUseCase {
        return FilterSearchUsersUseCase(apolloClient, schedulerProvider)
    }

    @PerFragment @Provides fun provideGetIssueUseCase(
        issueRepositoryProvider: IssueRepositoryProvider,
        apolloClient: ApolloClient,
        schedulerProvider: SchedulerProvider
    ): GetIssueUseCase {
        return GetIssueUseCase(issueRepositoryProvider, apolloClient, schedulerProvider)
    }

    @PerFragment @Provides fun provideGetIssueTimelineUseCase(
        issueRepositoryProvider: IssueRepositoryProvider,
        apolloClient: ApolloClient,
        schedulerProvider: SchedulerProvider
    ): GetIssueTimelineUseCase {
        return GetIssueTimelineUseCase(issueRepositoryProvider, apolloClient, schedulerProvider)
    }

    @PerFragment @Provides fun provideEditIssuePrUseCase(
        issueRepositoryProvider: IssueRepositoryProvider,
        issuePrService: IssuePrService,
        loginRepositoryProvider: LoginRepositoryProvider,
        schedulerProvider: SchedulerProvider
    ): CloseOpenIssuePrUseCase {
        return CloseOpenIssuePrUseCase(issueRepositoryProvider, issuePrService, loginRepositoryProvider, schedulerProvider)
    }

    @PerFragment @Provides fun provideLockUnlockIssuePrUseCase(
        issueRepositoryProvider: IssueRepositoryProvider,
        apolloClient: ApolloClient,
        loginRepositoryProvider: LoginRepositoryProvider,
        schedulerProvider: SchedulerProvider
    ): LockUnlockIssuePrUseCase {
        return LockUnlockIssuePrUseCase(issueRepositoryProvider, apolloClient, loginRepositoryProvider, schedulerProvider)
    }

    @PerFragment @Provides fun provideGetLabelsUseCase(
        apolloClient: ApolloClient,
        schedulerProvider: SchedulerProvider
    ): GetLabelsUseCase {
        return GetLabelsUseCase(apolloClient, schedulerProvider)
    }

    @PerFragment @Provides fun provideCreateLabelUseCase(
        repoService: RepoService,
        schedulerProvider: SchedulerProvider
    ): CreateLabelUseCase {
        return CreateLabelUseCase(repoService, schedulerProvider)
    }

    @PerFragment @Provides fun providePutLabelsUseCase(
        repoService: RepoService,
        schedulerProvider: SchedulerProvider
    ): PutLabelsUseCase {
        return PutLabelsUseCase(repoService, schedulerProvider)
    }

    @PerFragment @Provides fun provideGetAssigneesUseCase(
        apolloClient: ApolloClient,
        schedulerProvider: SchedulerProvider
    ): GetAssigneesUseCase {
        return GetAssigneesUseCase(apolloClient, schedulerProvider)
    }

    @PerFragment @Provides fun provideAddAssigneesUseCase(
        repoService: RepoService,
        schedulerProvider: SchedulerProvider
    ): AddAssigneesUseCase {
        return AddAssigneesUseCase(repoService, schedulerProvider)
    }

    @PerFragment @Provides fun provideGetMilestonesUseCase(
        apolloClient: ApolloClient,
        schedulerProvider: SchedulerProvider
    ): GetMilestonesUseCase {
        return GetMilestonesUseCase(apolloClient, schedulerProvider)
    }

    @PerFragment @Provides fun provideCreateMilestoneUseCase(
        repoService: RepoService,
        schedulerProvider: SchedulerProvider
    ): CreateMilestoneUseCase {
        return CreateMilestoneUseCase(repoService, schedulerProvider)
    }

    @PerFragment @Provides fun provideMilestoneIssuePrUseCase(
        issueRepositoryProvider: IssueRepositoryProvider,
        issuePrService: IssuePrService,
        loginRepositoryProvider: LoginRepositoryProvider,
        schedulerProvider: SchedulerProvider
    ): MilestoneIssuePrUseCase {
        return MilestoneIssuePrUseCase(issueRepositoryProvider, issuePrService, loginRepositoryProvider, schedulerProvider)
    }
}