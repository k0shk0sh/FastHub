package com.fastaccess.github.usecase.user

import com.fastaccess.data.persistence.models.UserModel
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.data.repository.UserRepository
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 10.06.18.
 */
class UserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {
    var login: String? = null

    override fun buildObservable(): Observable<UserModel> {
        val observable = login?.let { userRepository.getUserFromRemote(it) } ?: Observable.empty()
        return observable
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
    }

    fun getUser(login: String) = userRepository.getUser(login)
}