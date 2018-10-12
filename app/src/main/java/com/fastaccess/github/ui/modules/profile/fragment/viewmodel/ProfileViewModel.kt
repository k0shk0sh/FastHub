package com.fastaccess.github.ui.modules.profile.fragment.viewmodel

import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.user.UserUseCase
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kosh on 26.08.18.
 */
class ProfileViewModel @Inject constructor(private val userUseCase: UserUseCase) : BaseViewModel() {

    var isFirstLaunch = true

    fun getUser(login: String) = userUseCase.getUser(login)

    fun getUserFromRemote(login: String) {
        userUseCase.login = login
        add(callApi(userUseCase.buildObservable())
                .subscribe({
                    Timber.e("$it")
                }, {
                    it.printStackTrace()
                }))
    }

    override fun onCleared() {
        super.onCleared()
        userUseCase.dispose()
    }
}