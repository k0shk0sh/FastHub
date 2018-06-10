package com.fastaccess.github.di.modules

import com.fastaccess.data.persistence.db.FastHubLoginDatabase
import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.data.repository.services.LoginService
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
}