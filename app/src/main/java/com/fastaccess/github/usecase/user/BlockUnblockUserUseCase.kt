package com.fastaccess.github.usecase.user

import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.data.repository.UserRepository
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 10.06.18.
 */
class BlockUnblockUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {
    var login: String? = null
    var block: Boolean = false

    override fun buildObservable(): Observable<Boolean> = login?.let { login ->
        userRepository.blockUnblockUser(login, block)
            .map {
                val isSuccess = it.isSuccessful && it.code() == 204
                if (isSuccess) {
                    return@map block
                }
                return@map isSuccess
            }
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
    } ?: Observable.empty()
}