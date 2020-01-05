package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.fastaccess.data.model.GroupedNotificationsModel
import com.fastaccess.data.persistence.dao.NotificationsDao
import com.fastaccess.data.persistence.models.NotificationModel
import com.fastaccess.github.extensions.map
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject

/**
 * Created by Kosh on 22.06.18.
 */
class NotificationRepositoryProvider @Inject constructor(
    private val dao: NotificationsDao,
    private val schedulerProvider: SchedulerProvider
) : NotificationRepository {
    override fun getNotifications(unread: Boolean): DataSource.Factory<Int, NotificationModel> = dao.getNotifications(unread)
    override fun getAllNotifications(): LiveData<List<GroupedNotificationsModel>> = dao.getAllNotifications(false).map(groupNotifications())
    override fun getMainNotifications(): LiveData<List<NotificationModel>> = dao.getMainNotifications()
    override fun countUnread(): LiveData<Int> = dao.countUnread()
    override fun insert(model: NotificationModel) = dao.insert(model)
    override fun insert(model: List<NotificationModel>) = dao.insert(model)
    override fun update(model: NotificationModel) = dao.update(model)
    override fun delete(model: NotificationModel) = dao.delete(model)
    override fun deleteAll(unread: Boolean) = dao.deleteAll(unread)
    override fun markAsRead(id: String): Completable = Completable.fromCallable { dao.markAsRead(id) }
    override fun markAllAsRead(): Completable = Completable.fromCallable { dao.markAllAsRead() }
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())

    override fun getAllNotificationsAsMaybe(unread: Boolean): Maybe<List<NotificationModel>> = Maybe.just(dao.getAllNotificationsBlocking(unread))
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())

    /**
     * Fixes Cannot infer a type for this parameter. Please specify it explicitly. 🤷‍🤷‍🤷‍🤷‍🤷‍🤷‍
     */
    private fun groupNotifications(): (List<NotificationModel>) -> List<GroupedNotificationsModel> {
        return { list: List<NotificationModel> ->
            list.groupBy { it.repository }
                .flatMap { entry ->
                    val notifications = arrayListOf<GroupedNotificationsModel>()
                    notifications.add(GroupedNotificationsModel(GroupedNotificationsModel.HEADER, entry.key))
                    notifications.addAll(entry.value.map { GroupedNotificationsModel(GroupedNotificationsModel.CONTENT, notification = it) })
                    return@flatMap notifications
                }
        }
    }
}

interface NotificationRepository {
    fun getNotifications(unread: Boolean): DataSource.Factory<Int, NotificationModel>
    fun getAllNotifications(): LiveData<List<GroupedNotificationsModel>>
    fun getMainNotifications(): LiveData<List<NotificationModel>>
    fun countUnread(): LiveData<Int>
    fun insert(model: NotificationModel): Long
    fun insert(model: List<NotificationModel>): List<Long>
    fun update(model: NotificationModel)
    fun delete(model: NotificationModel)
    fun deleteAll(unread: Boolean)
    fun markAsRead(id: String): Completable
    fun markAllAsRead(): Completable
    fun getAllNotificationsAsMaybe(unread: Boolean): Maybe<List<NotificationModel>>
}