package com.fastaccess.data.persistence.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.fastaccess.data.persistence.models.NotificationModel

/**
 * Created by Kosh on 17.06.18.
 */
@Dao interface NotificationsDao {
    @Query("SELECT * FROM ${NotificationModel.TABLE_NAME}  WHERE `unread` = :unread")
    fun getNotifications(unread: Boolean): DataSource.Factory<Int, NotificationModel>

    @Query("SELECT * FROM ${NotificationModel.TABLE_NAME} WHERE `unread` = :unread ORDER BY `updatedAt` DESC")
    fun getAllNotifications(unread: Boolean): LiveData<List<NotificationModel>>

    @Query("SELECT * FROM ${NotificationModel.TABLE_NAME} WHERE `unread` = :unread ORDER BY `updatedAt` DESC")
    fun getAllNotificationsBlocking(unread: Boolean): List<NotificationModel>

    @Query("SELECT * FROM ${NotificationModel.TABLE_NAME} ORDER BY `updatedAt` DESC LIMIT 5")
    fun getMainNotifications(): LiveData<List<NotificationModel>>

    @Query("UPDATE ${NotificationModel.TABLE_NAME} SET `unread` = 0 WHERE `id` = :id")
    fun markAsRead(id: String)

    @Query("UPDATE ${NotificationModel.TABLE_NAME} SET `unread` = 0 WHERE `unread` = 1")
    fun markAllAsRead()

    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insert(model: NotificationModel): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insert(model: List<NotificationModel>)
    @Update(onConflict = OnConflictStrategy.REPLACE) fun update(model: NotificationModel): Int
    @Delete fun delete(model: NotificationModel)

    @Query("DELETE FROM ${NotificationModel.TABLE_NAME} WHERE `unread` = :unread") fun deleteAll(unread: Boolean)

    @Query("SELECT COUNT(*) FROM  ${NotificationModel.TABLE_NAME} WHERE `unread` = 0")
    fun countUnread(): LiveData<Int>
}