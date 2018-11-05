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

    @Query("SELECT * FROM ${NotificationModel.TABLE_NAME} LIMIT 5")
    fun getMainNotifications(): LiveData<List<NotificationModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insert(model: NotificationModel): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insert(model: List<NotificationModel>)
    @Update(onConflict = OnConflictStrategy.REPLACE) fun update(model: NotificationModel): Int
    @Delete fun delete(model: NotificationModel)

    @Query("DELETE FROM ${NotificationModel.TABLE_NAME} WHERE `unread` = :unread") fun deleteAll(unread: Boolean)
}