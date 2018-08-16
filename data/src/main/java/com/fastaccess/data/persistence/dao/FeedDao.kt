package com.fastaccess.data.persistence.dao

import androidx.room.Dao
import androidx.room.Query
import com.fastaccess.data.persistence.models.FeedModel
import io.reactivex.Maybe

/**
 * Created by Kosh on 06.07.18.
 */
@Dao
abstract class FeedDao : BaseDao<FeedModel>() {
    @Query("SELECT * FROM ${FeedModel.TABLE_NAME} WHERE `login` = :login")
    abstract fun getFeeds(login: String): Maybe<List<FeedModel>> // .distinctUntilChanged() in flowable seems broken /shrug

    @Query("SELECT * FROM ${FeedModel.TABLE_NAME} WHERE `login` = :login LIMIT 5")
    abstract fun getMainFeeds(login: String): Maybe<List<FeedModel>>

    @Query("DELETE FROM ${FeedModel.TABLE_NAME}") abstract fun deleteAll()

    @Query("DELETE FROM ${FeedModel.TABLE_NAME} WHERE `savedDate` <= datetime('now', '-7 day')") abstract fun deleteOldFeeds()
}