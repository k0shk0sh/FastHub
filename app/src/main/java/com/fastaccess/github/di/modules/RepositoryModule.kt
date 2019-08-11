package com.fastaccess.github.di.modules

import com.apollographql.apollo.ApolloClient
import com.fastaccess.data.persistence.db.FastHubDatabase
import com.fastaccess.data.persistence.db.FastHubLoginDatabase
import com.fastaccess.data.repository.*
import com.fastaccess.domain.repository.LoginRemoteRepository
import com.fastaccess.domain.repository.services.LoginService
import com.fastaccess.domain.repository.services.UserService
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Kosh on 11.05.18.
 */
@Module
class RepositoryModule {
    @Singleton @Provides fun provideLocalLoginRepository(
        fastHubLoginDatabase: FastHubLoginDatabase,
        loginService: LoginService
    ): LoginLocalRepository {
        return LoginRepositoryProvider(fastHubLoginDatabase.provideLoginDao(), loginService)
    }

    @Singleton @Provides fun provideRemoteLoginRepository(
        fastHubLoginDatabase: FastHubLoginDatabase,
        loginService: LoginService
    ): LoginRemoteRepository {
        return LoginRepositoryProvider(fastHubLoginDatabase.provideLoginDao(), loginService)
    }

    @Singleton @Provides fun provideUserRepository(
        fastHubDatabase: FastHubDatabase,
        apolloClient: ApolloClient,
        userService: UserService
    ): UserRepository {
        return UserRepositoryProvider(fastHubDatabase.getUserDao(), apolloClient, userService)
    }

    @Singleton @Provides fun provideMainIssuesPullsRepository(fastHubDatabase: FastHubDatabase): MyIssuesPullsRepository {
        return MyIssuesPullsRepositoryProvider(fastHubDatabase.getMainIssuesPullsDao())
    }

    @Singleton @Provides fun provideNotificationRepositoryProvider(
        fastHubDatabase: FastHubDatabase,
        schedulerProvider: SchedulerProvider
    ): NotificationRepository {
        return NotificationRepositoryProvider(fastHubDatabase.getNotifications(), schedulerProvider)
    }

    @Singleton @Provides fun provideFeedsRepositoryProvider(
        fastHubDatabase: FastHubDatabase,
        userService: UserService,
        loginRepositoryProvider: LoginLocalRepository,
        gson: Gson
    ): FeedsRepository {
        return FeedsRepositoryProvider(fastHubDatabase.getFeedsDao(), userService, loginRepositoryProvider, gson)
    }

    @Singleton @Provides fun provideUserReposRepositoryProvider(
        fastHubDatabase: FastHubDatabase,
        apolloClient: ApolloClient
    ): UserReposRepository {
        return UserReposRepositoryProvider(fastHubDatabase.getUserRepoDao(), apolloClient)
    }

    @Singleton @Provides fun provideUserStarredReposRepositoryProvider(
        fastHubDatabase: FastHubDatabase,
        apolloClient: ApolloClient
    ): UserStarredReposRepository {
        return UserStarredReposRepositoryProvider(fastHubDatabase.getUserStarredRepoDao(), apolloClient)
    }

    @Singleton @Provides fun provideUserGistsRepositoryProvider(
        fastHubDatabase: FastHubDatabase,
        apolloClient: ApolloClient
    ): UserGistsRepository {
        return UserGistsRepositoryProvider(fastHubDatabase.getGistsDao(), apolloClient)
    }

    @Singleton @Provides fun provideUserFollowersFollowingRepositoryProvider(
        fastHubDatabase: FastHubDatabase,
        apolloClient: ApolloClient
    ): FollowersFollowingRepository {
        return UserFollowersFollowingRepositoryProvider(fastHubDatabase.getFollowingFollowerDao(), apolloClient)
    }

    @Singleton @Provides fun provideOrgRepositoryProvider(
        fastHubDatabase: FastHubDatabase,
        apolloClient: ApolloClient,
        loginRepositoryProvider: LoginLocalRepository
    ): OrgRepository {
        return OrgRepositoryProvider(fastHubDatabase.getOrganizationDao(), apolloClient, loginRepositoryProvider)
    }

    @Singleton @Provides fun provideIssueRepository(fastHubDatabase: FastHubDatabase): IssueRepository {
        return IssueRepositoryProvider(fastHubDatabase.getIssueDao())
    }

    @Singleton @Provides fun provideSuggestionRepositoryProvider(fastHubDatabase: FastHubDatabase): SuggestionRepository {
        return SuggestionRepositoryProvider(fastHubDatabase.getSuggestionDao())
    }

    @Singleton @Provides fun provideAndroidSchedulerProvider(): SchedulerProvider = AndroidSchedulerProvider()

    @Singleton @Provides fun providePullRequestRepo(fastHubDatabase: FastHubDatabase): PullRequestRepository =
        PullRequestRepositoryProvider(fastHubDatabase.getPullRequestDao())
}