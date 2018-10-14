package com.fastaccess.github.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.github.di.annotations.ViewModelKey
import com.fastaccess.github.platform.viewmodel.FastHubViewModelFactory
import com.fastaccess.github.ui.modules.auth.login.LoginViewModel
import com.fastaccess.github.ui.modules.main.fragment.viewmodel.MainFragmentViewModel
import com.fastaccess.github.ui.modules.profile.fragment.viewmodel.ProfileViewModel
import com.fastaccess.github.ui.modules.profile.gists.viewmodel.ProfileGistsViewModel
import com.fastaccess.github.ui.modules.profile.repos.viewmodel.ProfileReposViewModel
import com.fastaccess.github.ui.modules.profile.starred.viewmodel.ProfileStarredReposViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Kosh on 21.05.18.
 */
@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds @IntoMap @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds @IntoMap @ViewModelKey(MainFragmentViewModel::class)
    abstract fun bindMainFragmentViewModel(viewModel: MainFragmentViewModel): ViewModel

    @Binds @IntoMap @ViewModelKey(ProfileViewModel::class)
    abstract fun bindProfileViewModel(viewModel: ProfileViewModel): ViewModel

    @Binds @IntoMap @ViewModelKey(ProfileReposViewModel::class)
    abstract fun bindProfileReposViewModel(viewModel: ProfileReposViewModel): ViewModel

    @Binds @IntoMap @ViewModelKey(ProfileStarredReposViewModel::class)
    abstract fun bindProfileStarredReposViewModel(viewModel: ProfileStarredReposViewModel): ViewModel

    @Binds @IntoMap @ViewModelKey(ProfileGistsViewModel::class)
    abstract fun bindProfileGistsViewModel(viewModel: ProfileGistsViewModel): ViewModel

    @Binds abstract fun bindViewModelFactory(factoryFastHub: FastHubViewModelFactory): ViewModelProvider.Factory

}