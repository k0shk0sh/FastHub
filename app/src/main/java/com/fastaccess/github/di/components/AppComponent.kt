package com.fastaccess.github.di.components

import android.app.Application
import com.fastaccess.data.di.module.FastHubDatabaseModule
import com.fastaccess.data.di.module.NetworkModule
import com.fastaccess.github.App
import com.fastaccess.github.di.modules.ActivityBindingModule
import com.fastaccess.github.di.modules.ApplicationModule
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
    ActivityBindingModule::class,
    ApplicationModule::class,
    FastHubDatabaseModule::class,
    NetworkModule::class,
    AndroidSupportInjectionModule::class])
interface AppComponent : AndroidInjector<DaggerApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance fun application(application: Application): Builder

        @BindsInstance fun fastHubDatabaseModule(fastHubDatabaseModule: FastHubDatabaseModule): Builder

        @BindsInstance fun networkModule(networkModule: NetworkModule): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)

    override fun inject(instance: DaggerApplication)

    companion object {
        fun inject(app: App) {
            DaggerAppComponent.builder()
                    .application(app)
                    .fastHubDatabaseModule(FastHubDatabaseModule())
                    .networkModule(NetworkModule(/*TODO(add url)*/))
                    .build()
                    .inject(app)
        }
    }
}