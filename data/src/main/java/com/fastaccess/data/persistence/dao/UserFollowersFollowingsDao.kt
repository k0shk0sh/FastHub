package com.fastaccess.data.persistence.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.fastaccess.data.persistence.models.FollowingFollowerModel

/**
 * Created by Kosh on 06.07.18.
 */
@Dao
abstract class UserFollowersFollowingsDao : BaseDao<FollowingFollowerModel>() {
    @Query("SELECT * FROM ${FollowingFollowerModel.TABLE_NAME} WHERE `currentLogin` = :login AND `isFollowers` = 1")
    abstract fun getFollowers(login: String): DataSource.Factory<Int, FollowingFollowerModel>

    @Query("SELECT * FROM ${FollowingFollowerModel.TABLE_NAME} WHERE `currentLogin` = :login  AND `isFollowers` = 0")
    abstract fun getFollowing(login: String): DataSource.Factory<Int, FollowingFollowerModel>

    @Query("SELECT * FROM ${FollowingFollowerModel.TABLE_NAME} WHERE `login` = :login")
    abstract fun getUser(login: String): LiveData<FollowingFollowerModel>

    @Query("DELETE FROM ${FollowingFollowerModel.TABLE_NAME}") abstract fun deleteAll()
    @Query("DELETE FROM ${FollowingFollowerModel.TABLE_NAME} WHERE `currentLogin` = :login AND `isFollowers` = :isFollowers")
    abstract fun deleteAll(login: String, isFollowers: Boolean)
}