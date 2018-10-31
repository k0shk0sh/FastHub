package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.fastaccess.data.persistence.dao.NotificationsDao
import com.fastaccess.data.persistence.models.NotificationModel
import javax.inject.Inject

/**
 * Created by Kosh on 22.06.18.
 */
class NotificationRepositoryProvider @Inject constructor(private val dao: NotificationsDao) : NotificationRepository {
    override fun getNotifications(unread: Boolean): DataSource.Factory<Int, NotificationModel> = dao.getNotifications(unread)
    override fun getMainNotifications(): LiveData<List<NotificationModel>> = dao.getMainNotifications()
    override fun insert(model: NotificationModel): Long = dao.insert(model)
    override fun insert(model: List<NotificationModel>) = dao.insert(model)
    override fun update(model: NotificationModel): Int = dao.update(model)
    override fun delete(model: NotificationModel) = dao.delete(model)
    override fun deleteAll(unread: Boolean) = dao.deleteAll(unread)
}

interface NotificationRepository {
    fun getNotifications(unread: Boolean): DataSource.Factory<Int, NotificationModel>
    fun getMainNotifications(): LiveData<List<NotificationModel>>
    fun insert(model: NotificationModel): Long
    fun insert(model: List<NotificationModel>)
    fun update(model: NotificationModel): Int
    fun delete(model: NotificationModel)
    fun deleteAll(unread: Boolean)
}