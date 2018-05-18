package com.fastaccess.github.di.modules

import android.app.Application
import android.content.Context
import com.fastaccess.data.di.annotations.ForApplication
import dagger.Module
import dagger.Provides

/**
 * Created by Kosh on 12.05.18.
 */

@Module
class ApplicationModule {

    @Provides @ForApplication fun providesApplicationContext(application: Application): Context {
        return application
    }

//    @Provides fun provideGlide(@ForApplication context: Context): GlideRequests = GlideApp.with(context)
}