package com.fastaccess.github.di.modules

import com.fastaccess.domain.di.scopes.PerActivity
import com.fastaccess.github.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Kosh on 12.05.18.
 */
@Module
abstract class ActivityBindingModule {
    @PerActivity @ContributesAndroidInjector(modules = [ActivityModule::class]) abstract fun mainActivity(): MainActivity
}