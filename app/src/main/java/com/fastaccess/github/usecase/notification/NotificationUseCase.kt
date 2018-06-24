package com.fastaccess.github.usecase.notification

import com.fastaccess.data.persistence.models.NotificationModel
import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.data.repository.NotificationRepositoryProvider
import com.fastaccess.data.repository.services.NotificationService
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.google.gson.Gson
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 22.06.18.
 */


class NotificationUseCase @Inject constructor(private val notificationRepositoryProvider: NotificationRepositoryProvider,
                                              private val notificationService: NotificationService,
                                              private val loginRepositoryProvider: LoginRepositoryProvider,
                                              private val gson: Gson) : BaseObservableUseCase() {
    override fun buildObservable(): Observable<*> = notificationService.getMainNotifications()
            .map {
                notificationRepositoryProvider.deleteAll()
                val me = loginRepositoryProvider.getLoginBlocking()
                it.items?.forEach {
                    val item = NotificationModel.convert(gson, it)
                    item.login = me?.login
                    notificationRepositoryProvider.insert(item)
                }
            }

    fun getMainNotifications(login: String) = notificationRepositoryProvider.getMainNotifications(login)
}