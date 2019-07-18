package com.fastaccess.github.di.modules

import com.apollographql.apollo.ApolloClient
import com.fastaccess.data.persistence.db.FastHubDatabase
import com.fastaccess.data.persistence.db.FastHubLoginDatabase
import com.fastaccess.data.repository.*
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
    @Singleton @Provides fun provideLoginRepository(
        fastHubLoginDatabase: FastHubLoginDatabase,
        loginService: LoginService
    ): LoginRepositoryProvider {
        return LoginRepositoryProvider(fastHubLoginDatabase.provideLoginDao(), loginService)
    }

    @Singleton @Provides fun provideUserRepository(
        fastHubDatabase: FastHubDatabase,
        apolloClient: ApolloClient,
        userService: UserService
    ): UserRepositoryProvider {
        return UserRepositoryProvider(fastHubDatabase.getUserDao(), apolloClient, userService)
    }

    @Singleton @Provides fun provideMainIssuesPullsRepository(fastHubDatabase: FastHubDatabase): MyIssuesPullsRepositoryProvider {
        return MyIssuesPullsRepositoryProvider(fastHubDatabase.getMainIssuesPullsDao())
    }

    @Singleton @Provides fun provideNotificationRepositoryProvider(
        fastHubDatabase: FastHubDatabase,
        schedulerProvider: SchedulerProvider
    ): NotificationRepositoryProvider {
        return NotificationRepositoryProvider(fastHubDatabase.getNotifications(), schedulerProvider)
    }

    @Singleton @Provides fun provideFeedsRepositoryProvider(
        fastHubDatabase: FastHubDatabase,
        userService: UserService,
        loginRepositoryProvider: LoginRepositoryProvider,
        gson: Gson
    ): FeedsRepositoryProvider {
        return FeedsRepositoryProvider(fastHubDatabase.getFeedsDao(), userService, loginRepositoryProvider, gson)
    }

    @Singleton @Provides fun provideUserReposRepositoryProvider(
        fastHubDatabase: FastHubDatabase,
        apolloClient: ApolloClient
    ): UserReposRepositoryProvider {
        return UserReposRepositoryProvider(fastHubDatabase.getUserRepoDao(), apolloClient)
    }

    @Singleton @Provides fun provideUserStarredReposRepositoryProvider(
        fastHubDatabase: FastHubDatabase,
        apolloClient: ApolloClient
    ): UserStarredReposRepositoryProvider {
        return UserStarredReposRepositoryProvider(fastHubDatabase.getUserStarredRepoDao(), apolloClient)
    }

    @Singleton @Provides fun provideUserGistsRepositoryProvider(
        fastHubDatabase: FastHubDatabase,
        apolloClient: ApolloClient
    ): UserGistsRepositoryProvider {
        return UserGistsRepositoryProvider(fastHubDatabase.getGistsDao(), apolloClient)
    }

    @Singleton @Provides fun provideUserFollowersFollowingRepositoryProvider(
        fastHubDatabase: FastHubDatabase,
        apolloClient: ApolloClient
    ): UserFollowersFollowingRepositoryProvider {
        return UserFollowersFollowingRepositoryProvider(fastHubDatabase.getFollowingFollowerDao(), apolloClient)
    }

    @Singleton @Provides fun provideOrgRepositoryProvider(
        fastHubDatabase: FastHubDatabase,
        apolloClient: ApolloClient,
        loginRepositoryProvider: LoginRepositoryProvider
    ): OrgRepositoryProvider {
        return OrgRepositoryProvider(fastHubDatabase.getOrganizationDao(), apolloClient, loginRepositoryProvider)
    }

    @Singleton @Provides fun provideIssueRepository(fastHubDatabase: FastHubDatabase): IssueRepositoryProvider {
        return IssueRepositoryProvider(fastHubDatabase.getIssueDao())
    }

    @Singleton @Provides fun provideSuggestionRepositoryProvider(fastHubDatabase: FastHubDatabase): SuggestionRepositoryProvider {
        return SuggestionRepositoryProvider(fastHubDatabase.getSuggestionDao())
    }

    @Singleton @Provides fun provideAndroidSchedulerProvider(): SchedulerProvider = AndroidSchedulerProvider()
}