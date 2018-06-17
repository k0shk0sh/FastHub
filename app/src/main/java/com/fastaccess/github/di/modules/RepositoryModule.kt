package com.fastaccess.github.di.modules

import com.fastaccess.data.persistence.db.FastHubDatabase
import com.fastaccess.data.persistence.db.FastHubLoginDatabase
import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.data.repository.MainIssuesPullsRepositoryProvider
import com.fastaccess.data.repository.UserRepositoryProvider
import com.fastaccess.data.repository.services.LoginService
import com.fastaccess.data.repository.services.UserService
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Kosh on 11.05.18.
 */
@Module
class RepositoryModule {
    @Singleton @Provides fun provideLoginRepository(fastHubLoginDatabase: FastHubLoginDatabase, loginService: LoginService): LoginRepositoryProvider {
        return LoginRepositoryProvider(fastHubLoginDatabase.provideLoginDao(), loginService)
    }

    @Singleton @Provides fun provideUserRepository(userService: UserService,
                                                   gson: Gson): UserRepositoryProvider {
        return UserRepositoryProvider(userService, gson)
    }

    @Singleton @Provides fun provideMainIssuesPullsRepository(fastHubDatabase: FastHubDatabase): MainIssuesPullsRepositoryProvider {
        return MainIssuesPullsRepositoryProvider(fastHubDatabase.getMainIssuesPullsDao())
    }
}