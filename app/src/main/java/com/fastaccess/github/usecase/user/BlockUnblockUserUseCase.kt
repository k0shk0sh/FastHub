package com.fastaccess.github.usecase.user

import com.fastaccess.data.repository.UserRepositoryProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 10.06.18.
 */
class BlockUnblockUserUseCase @Inject constructor(private val userRepository: UserRepositoryProvider) : BaseObservableUseCase() {
    var login: String? = null
    var block: Boolean = false

    override fun buildObservable(): Observable<Boolean> = login?.let { login ->
        userRepository.blockUnblockUser(login, block)
            .map { it.isSuccessful && it.code() == 200 }
    } ?: Observable.empty()
}