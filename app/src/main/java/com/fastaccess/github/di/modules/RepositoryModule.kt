package com.fastaccess.github.di.modules

import com.fastaccess.data.persistence.db.FastHubLoginDatabase
import com.fastaccess.data.repository.LoginLocalRepositoryProvider
import com.fastaccess.data.repository.services.LoginService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Kosh on 11.05.18.
 */
@Module
class RepositoryModule {
    @Singleton @Provides fun provideLoginRepository(fastHubLoginDatabase: FastHubLoginDatabase, loginService: LoginService): LoginLocalRepositoryProvider {
        return LoginLocalRepositoryProvider(fastHubLoginDatabase.provideLoginDao(), loginService)
    }
}