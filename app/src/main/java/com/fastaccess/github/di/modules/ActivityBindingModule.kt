package com.fastaccess.github.di.modules

import com.fastaccess.github.ui.modules.main.MainActivity
import com.fastaccess.github.di.scopes.PerActivity
import com.fastaccess.github.ui.modules.auth.LoginChooserActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Kosh on 12.05.18.
 */
@Module
abstract class ActivityBindingModule {
    @PerActivity @ContributesAndroidInjector(modules = [FragmentBindingModule::class]) abstract fun mainActivity(): MainActivity
    @PerActivity @ContributesAndroidInjector(modules = [FragmentBindingModule::class]) abstract fun loginChooser(): LoginChooserActivity
}