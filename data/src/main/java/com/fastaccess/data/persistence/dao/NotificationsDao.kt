package com.fastaccess.data.persistence.dao

import androidx.room.*
import com.fastaccess.data.persistence.models.NotificationModel
import io.reactivex.Maybe

/**
 * Created by Kosh on 17.06.18.
 */
@Dao interface NotificationsDao {
    @Query("SELECT * FROM ${NotificationModel.TABLE_NAME} WHERE `login` = :login")
    fun getNotifications(login: String): Maybe<List<NotificationModel>>

    @Query("SELECT * FROM ${NotificationModel.TABLE_NAME} WHERE `login` = :login LIMIT 5")
    fun getMainNotifications(login: String): Maybe<List<NotificationModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insert(model: NotificationModel): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insert(model: List<NotificationModel>)
    @Update(onConflict = OnConflictStrategy.REPLACE) fun update(model: NotificationModel): Int
    @Delete fun delete(model: NotificationModel)
    @Query("DELETE FROM ${NotificationModel.TABLE_NAME}") fun deleteAll()
}