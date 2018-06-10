package com.fastaccess.github.usecase.user

import com.fastaccess.data.repository.UserRepositoryProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 10.06.18.
 */
class UserUseCase @Inject constructor(private val userRepository: UserRepositoryProvider) : BaseObservableUseCase() {
    var type: Int = 0
    override fun buildObservable(): Observable<*> {
        return when (type) {
            0 -> userRepository.getUser()
            else -> userRepository.getUser()
        }
    }
}