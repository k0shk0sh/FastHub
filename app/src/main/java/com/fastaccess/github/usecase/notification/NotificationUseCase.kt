package com.fastaccess.github.usecase.notification

import com.fastaccess.data.persistence.models.NotificationModel
import com.fastaccess.data.repository.NotificationRepositoryProvider
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.repository.services.NotificationService
import com.fastaccess.domain.response.NotificationResponse
import com.fastaccess.domain.response.PageableResponse
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.github.extensions.getLastWeekDate
import com.google.gson.Gson
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 22.06.18.
 */

class NotificationUseCase @Inject constructor(
    private val notificationRepositoryProvider: NotificationRepositoryProvider,
    private val notificationService: NotificationService,
    private val gson: Gson,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var page: Int? = null
    var all: Boolean? = null

    override fun buildObservable(): Observable<PageableResponse<NotificationResponse>> {
        val page = page
        val all = all
        val observable = if (all == true) {
            notificationService.getAllNotifications()
        } else {
            if (page == null) {
                notificationService.getMainNotifications()
            } else {
                notificationService.getNotifications(getLastWeekDate(), page)
            }
        }
        return observable
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
            .map { it ->
                it.items?.let { items ->
                    if (all == true) {
                        notificationRepositoryProvider.deleteAll(false)
                        val list = items.asSequence().filter { it.unread == false }.toList()
                        notificationRepositoryProvider.insert(NotificationModel.convert(gson, list))
                        return@map it
                    }
                    if (page ?: 0 <= 1) notificationRepositoryProvider.deleteAll(true)
                    notificationRepositoryProvider.insert(NotificationModel.convert(gson, items))
                }
                return@map it
            }
    }
}