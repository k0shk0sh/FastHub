package com.fastaccess.github.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.github.di.annotations.ViewModelKey
import com.fastaccess.github.platform.viewmodel.FastHubViewModelFactory
import com.fastaccess.github.ui.modules.auth.login.LoginViewModel
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
    abstract fun bindLoginViewModel(mainViewModel: LoginViewModel): ViewModel

    @Binds abstract fun bindViewModelFactory(factoryFastHub: FastHubViewModelFactory): ViewModelProvider.Factory

}