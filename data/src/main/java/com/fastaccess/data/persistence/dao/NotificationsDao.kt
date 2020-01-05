package com.fastaccess.data.persistence.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.fastaccess.data.persistence.models.NotificationModel

/**
 * Created by Kosh on 17.06.18.
 */
@Dao abstract class NotificationsDao : BaseDao<NotificationModel>() {
    @Query("SELECT * FROM ${NotificationModel.TABLE_NAME}  WHERE `unread` = :unread ORDER BY `updatedAt` DESC")
    abstract fun getNotifications(unread: Boolean): DataSource.Factory<Int, NotificationModel>

    @Query("SELECT * FROM ${NotificationModel.TABLE_NAME} WHERE `unread` = :unread ORDER BY `updatedAt` DESC")
    abstract fun getAllNotifications(unread: Boolean): LiveData<List<NotificationModel>>

    @Query("SELECT * FROM ${NotificationModel.TABLE_NAME} WHERE `unread` = :unread ORDER BY `updatedAt` DESC")
    abstract fun getAllNotificationsBlocking(unread: Boolean): List<NotificationModel>

    @Query("SELECT * FROM ${NotificationModel.TABLE_NAME} WHERE `unread` = 1 ORDER BY `updatedAt` DESC LIMIT 5")
    abstract fun getMainNotifications(): LiveData<List<NotificationModel>>

    @Query("UPDATE ${NotificationModel.TABLE_NAME} SET `unread` = 0 WHERE `id` = :id")
    abstract fun markAsRead(id: String)

    @Query("UPDATE ${NotificationModel.TABLE_NAME} SET `unread` = 0 WHERE `unread` = 1")
    abstract fun markAllAsRead()

    @Query("DELETE FROM ${NotificationModel.TABLE_NAME} WHERE `unread` = :unread") abstract fun deleteAll(unread: Boolean)

    @Query("SELECT COUNT(*) FROM  ${NotificationModel.TABLE_NAME} WHERE `unread` = 1")
    abstract fun countUnread(): LiveData<Int>
}