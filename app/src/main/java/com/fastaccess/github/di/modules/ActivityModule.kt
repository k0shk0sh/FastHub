package com.fastaccess.github.di.modules

import android.content.Context
import com.fastaccess.data.di.annotations.ForApplication
import com.fastaccess.data.storage.FastHubSharedPreference
import com.fastaccess.domain.di.scopes.PerActivity
import com.fastaccess.github.base.engine.ThemeEngine
import dagger.Module
import dagger.Provides

/**
 * Created by Kosh on 18.05.18.
 */
@Module
class ActivityModule {

    @Provides @PerActivity fun provideThemeEngine(@ForApplication context: Context,
                                                  preference: FastHubSharedPreference) = ThemeEngine(context.resources, preference)
}