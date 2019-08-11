package com.fastaccess.github.usecase.user

import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.data.repository.UserRepository
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 10.06.18.
 */
class IsUserBlockedUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {
    var login: String? = null

    override fun buildObservable(): Observable<Boolean> = login?.let { login ->
        userRepository.isUserBlocked(login)
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
            .map { it.isSuccessful && it.code() == 204 }
    } ?: Observable.empty()
}