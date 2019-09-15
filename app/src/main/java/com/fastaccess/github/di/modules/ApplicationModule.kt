package com.fastaccess.github.di.modules

import android.app.Application
import android.content.Context
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.fasthub.dagger.annotations.ForApplication
import com.fastaccess.github.base.extensions.theme
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Kosh on 12.05.18.
 */

@Module(includes = [ViewModelModule::class])
class ApplicationModule {

    @Provides @ForApplication fun providesApplicationContext(application: Application): Context {
        return application
    }

    @Provides @Named("theme") @Singleton fun provideTheme(preference: FastHubSharedPreference) = preference.theme

//    @Provides fun provideGlide(@ForApplication context: Context): GlideRequests = GlideApp.with(context)
}