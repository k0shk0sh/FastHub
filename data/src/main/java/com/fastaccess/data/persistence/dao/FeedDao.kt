package com.fastaccess.data.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fastaccess.data.persistence.models.FeedModel

/**
 * Created by Kosh on 06.07.18.
 */
@Dao
interface FeedDao {
    @Query("SELECT * FROM ${FeedModel.TABLE_NAME} WHERE `login` = :login")
    fun getNotifications(login: String): LiveData<List<FeedModel>>

    @Query("SELECT * FROM ${FeedModel.TABLE_NAME} WHERE `login` = :login LIMIT 5")
    fun getMainNotifications(login: String): LiveData<List<FeedModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insert(model: FeedModel): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insert(model: List<FeedModel>)
    @Update(onConflict = OnConflictStrategy.REPLACE) fun update(model: FeedModel): Int
    @Delete fun delete(model: FeedModel)
    @Query("DELETE FROM ${FeedModel.TABLE_NAME}") fun deleteAll()
}