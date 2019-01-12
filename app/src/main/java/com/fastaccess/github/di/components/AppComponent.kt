package com.fastaccess.github.di.components

import android.app.Application
import com.fastaccess.github.App
import com.fastaccess.github.di.modules.*
import com.fastaccess.github.platform.workmanager.DaggerWorkerFactory
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication
import javax.inject.Singleton

/**
 * Created by Kosh on 12.05.18.
 */
@Singleton
@Component(modules = [
    ApplicationModule::class,
    FastHubDatabaseModule::class,
    NetworkModule::class,
    RepositoryModule::class,
    ActivityBindingModule::class,
    ActivityModule::class,
    RepositoryModule::class,
    AndroidSupportInjectionModule::class])
interface AppComponent : AndroidInjector<DaggerApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance fun application(application: Application): Builder

        @BindsInstance fun fastHubDatabaseModule(fastHubDatabaseModule: FastHubDatabaseModule): Builder

        @BindsInstance fun networkModule(networkModule: NetworkModule): Builder

        @BindsInstance fun activityModule(activityModule: ActivityModule): Builder

        @BindsInstance fun repoModule(repositoryModule: RepositoryModule): Builder

        @BindsInstance fun repositoryModule(repositoryModule: RepositoryModule): Builder

        fun build(): AppComponent
    }

    fun daggerWorkerFactory(): DaggerWorkerFactory

    fun workerSubComponentBuilder(): WorkerSubComponent.Builder

    fun inject(app: App)

    override fun inject(instance: DaggerApplication)

    companion object {
        fun getComponent(app: App): AppComponent = DaggerAppComponent.builder()
                .application(app)
                .fastHubDatabaseModule(FastHubDatabaseModule())
                .networkModule(NetworkModule())
                .activityModule(ActivityModule())
                .repoModule(RepositoryModule())
                .repositoryModule(RepositoryModule())
                .build()
    }
}