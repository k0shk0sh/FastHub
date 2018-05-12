package com.fastaccess.data.di.module

import com.fastaccess.data.persistence.db.FastHubLoginDatabase
import com.fastaccess.data.repository.LoginRepository
import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.data.repository.services.LoginService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Kosh on 11.05.18.
 */
@Module(includes = [FastHubDatabaseModule::class, NetworkModule::class])
class RepositoryModule {
    @Singleton @Provides fun provideLoginRepository(fastHubLoginDatabase: FastHubLoginDatabase, loginService: LoginService): LoginRepository {
        return LoginRepositoryProvider(fastHubLoginDatabase.provideLoginDao(), loginService)
    }
}