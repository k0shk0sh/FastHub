package com.fastaccess.github.usecase.user

import com.fastaccess.data.repository.UserRepositoryProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 10.06.18.
 */
class FollowUnfollowUserUseCase @Inject constructor(private val userRepository: UserRepositoryProvider) : BaseObservableUseCase() {
    var login: String? = null
    var follow: Boolean = false

    override fun buildObservable(): Observable<Boolean> = login?.let { login ->
        userRepository.followUnfollowUser(login, follow)
            .map { response ->
                val isSuccess = response.isSuccessful && response.code() == 204
                if (isSuccess) {
                    userRepository.getUserBlocking(login)?.let {
                        it.viewerIsFollowing = follow
                        userRepository.updateUser(it)
                    }
                    return@map follow
                }
                return@map isSuccess
            }
    } ?: Observable.empty()
}