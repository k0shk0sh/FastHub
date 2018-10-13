package com.fastaccess.github.di.modules

import com.fastaccess.github.di.scopes.PerFragment
import com.fastaccess.github.ui.modules.auth.chooser.LoginChooserFragment
import com.fastaccess.github.ui.modules.auth.login.AuthLoginFragment
import com.fastaccess.github.ui.modules.main.fragment.MainFragment
import com.fastaccess.github.ui.modules.profile.fragment.ProfileFragment
import com.fastaccess.github.ui.modules.profile.repos.ProfileReposFragment
import com.fastaccess.github.ui.modules.profile.starred.ProfileStarredReposFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Kosh on 19.05.18.
 */
@Suppress("unused")
@Module
abstract class FragmentBindingModule {
    @PerFragment @ContributesAndroidInjector abstract fun provideLoginChooseFragment(): LoginChooserFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideLoginFragment(): AuthLoginFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideMainFragment(): MainFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideProfileFragment(): ProfileFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideProfileReposFragment(): ProfileReposFragment
    @PerFragment @ContributesAndroidInjector abstract fun provideProfileStarredeReposFragment(): ProfileStarredReposFragment
}