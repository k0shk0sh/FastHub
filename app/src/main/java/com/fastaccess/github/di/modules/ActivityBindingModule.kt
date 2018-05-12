package com.fastaccess.github.di.modules

import com.fastaccess.domain.di.scopes.PerActivity
import com.fastaccess.github.MainActivity
import com.fastaccess.github.base.BaseActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Kosh on 12.05.18.
 */
@Module
abstract class ActivityBindingModule {

    @PerActivity @ContributesAndroidInjector abstract fun baseActivity(): BaseActivity

    @PerActivity @ContributesAndroidInjector abstract fun mainActivity(): MainActivity
}