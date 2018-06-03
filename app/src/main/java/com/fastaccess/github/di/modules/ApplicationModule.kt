package com.fastaccess.github.di.modules

import android.app.Application
import android.content.Context
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.github.base.engine.ThemeEngine
import com.fastaccess.github.di.annotations.ForApplication
import dagger.Module
import dagger.Provides

/**
 * Created by Kosh on 12.05.18.
 */

@Module(includes = [ViewModelModule::class])
class ApplicationModule {

    @Provides @ForApplication fun providesApplicationContext(application: Application): Context {
        return application
    }

    @Provides @ForApplication fun provideThemeEngine(preference: FastHubSharedPreference) = ThemeEngine(preference)

//    @Provides fun provideGlide(@ForApplication context: Context): GlideRequests = GlideApp.with(context)
}