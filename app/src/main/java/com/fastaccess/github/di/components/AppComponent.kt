package com.fastaccess.github.di.components

import android.app.Application
import com.fastaccess.data.persistence.db.FastHubDatabase
import com.fastaccess.data.persistence.db.FastHubLoginDatabase
import com.fastaccess.github.App
import com.fastaccess.github.di.modules.*
import com.fastaccess.github.platform.workmanager.DaggerWorkerFactory
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import javax.inject.Singleton

/**
 * Created by Kosh on 12.05.18.
 */
@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        FastHubDatabaseModule::class,
        NetworkModule::class,
        RepositoryModule::class,
        ActivityBindingModule::class,
        RepositoryModule::class ]
)
interface AppComponent : AndroidInjector<DaggerApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance fun application(application: Application): Builder
        @BindsInstance fun fastHubDatabaseModule(module: FastHubDatabaseModule): Builder
        @BindsInstance fun networkModule(module: NetworkModule): Builder
        @BindsInstance fun repoModule(module: RepositoryModule): Builder
        @BindsInstance fun repositoryModule(module: RepositoryModule): Builder

        fun build(): AppComponent
    }

    fun daggerWorkerFactory(): DaggerWorkerFactory
    fun workerSubComponentBuilder(): WorkerSubComponent.Builder
    fun fastHubDatabase(): FastHubDatabase
    fun fasthubLoginDatabase(): FastHubLoginDatabase

    companion object {
        fun getComponent(app: App): AppComponent = DaggerAppComponent.builder()
            .application(app)
            .fastHubDatabaseModule(FastHubDatabaseModule())
            .networkModule(NetworkModule())
            .repoModule(RepositoryModule())
            .repositoryModule(RepositoryModule())
            .build()
    }
}