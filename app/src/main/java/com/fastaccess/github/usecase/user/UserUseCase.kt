package com.fastaccess.github.usecase.user

import com.fastaccess.data.persistence.models.UserModel
import com.fastaccess.data.repository.UserRepositoryProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 10.06.18.
 */
class UserUseCase @Inject constructor(private val userRepository: UserRepositoryProvider) : BaseObservableUseCase() {
    var login: String? = null

    override fun buildObservable(): Observable<UserModel> = login?.let { userRepository.getUserFromRemote(it) } ?: Observable.empty()

    fun getUser(login: String) = userRepository.getUser(login)
}