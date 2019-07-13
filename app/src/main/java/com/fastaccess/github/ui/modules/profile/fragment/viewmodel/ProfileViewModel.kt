package com.fastaccess.github.ui.modules.profile.fragment.viewmodel

import androidx.lifecycle.MutableLiveData
import com.fastaccess.extension.uiThread
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.user.BlockUnblockUserUseCase
import com.fastaccess.github.usecase.user.FollowUnfollowUserUseCase
import com.fastaccess.github.usecase.user.IsUserBlockedUseCase
import com.fastaccess.github.usecase.user.UserUseCase
import javax.inject.Inject

/**
 * Created by Kosh on 26.08.18.
 */
class ProfileViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val unblockUserUseCase: BlockUnblockUserUseCase,
    private val blockedUseCase: IsUserBlockedUseCase,
    private val followUnfollowUserUseCase: FollowUnfollowUserUseCase
) : BaseViewModel() {

    var isFirstLaunch = true
    val isBlocked = MutableLiveData<Boolean>()

    fun getUser(login: String) = userUseCase.getUser(login)

    fun getUserFromRemote(login: String) {
        userUseCase.login = login
        add(callApi(userUseCase.buildObservable())
            .subscribe({}, { it.printStackTrace() }))
    }

    fun checkBlockingState(login: String) {
        blockedUseCase.login = login
        add(blockedUseCase.buildObservable()
            .uiThread()
            .subscribe({
                isBlocked.postValue(it)
            }, {
                isBlocked.postValue(false)
                it.printStackTrace()
            }))
    }

    fun blockUnblockUser(login: String) {
        unblockUserUseCase.login = login
        unblockUserUseCase.block = isBlocked.value == false
        add(callApi(unblockUserUseCase.buildObservable())
            .subscribe({ isBlocked.postValue(it) }, { it.printStackTrace() }))
    }

    fun followUnfollowUser(login: String, viewerIsFollowing: Boolean?) {
        followUnfollowUserUseCase.login = login
        followUnfollowUserUseCase.follow = viewerIsFollowing == false
        justSubscribe(followUnfollowUserUseCase.buildObservable())
    }

    override fun onCleared() {
        super.onCleared()
        userUseCase.dispose()
        unblockUserUseCase.dispose()
        blockedUseCase.dispose()
    }
}