package com.fastaccess.github.di.modules

import com.fastaccess.github.di.scopes.PerFragment
import com.fastaccess.github.ui.modules.auth.chooser.LoginChooserFragment
import com.fastaccess.github.ui.modules.auth.login.BaseAuthLoginFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Kosh on 19.05.18.
 */
@Module
abstract class FragmentBindingModule {
    @PerFragment @ContributesAndroidInjector abstract fun provideLoginChooseFragment(): LoginChooserFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideLoginFragment(): BaseAuthLoginFragment
}