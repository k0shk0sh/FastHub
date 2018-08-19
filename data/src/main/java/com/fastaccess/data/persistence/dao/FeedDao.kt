package com.fastaccess.data.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.fastaccess.data.persistence.models.FeedModel

/**
 * Created by Kosh on 06.07.18.
 */
@Dao
abstract class FeedDao : BaseDao<FeedModel>() {
    @Query("SELECT * FROM ${FeedModel.TABLE_NAME} WHERE `login` = :login  ORDER BY `savedDate` ASC")
    abstract fun getFeeds(login: String): LiveData<List<FeedModel>> // .distinctUntilChanged() in flowable seems broken /shrug

    @Query("SELECT * FROM ${FeedModel.TABLE_NAME} WHERE `login` = :login ORDER BY `savedDate` ASC LIMIT 5")
    abstract fun getMainFeeds(login: String): LiveData<List<FeedModel>>

    @Query("DELETE FROM ${FeedModel.TABLE_NAME}") abstract fun deleteAll()

    @Query("DELETE FROM ${FeedModel.TABLE_NAME} WHERE `savedDate` <= datetime('now', '-7 day')") abstract fun deleteOldFeeds()
}