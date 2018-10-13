package com.fastaccess.data.persistence.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.fastaccess.data.persistence.models.ProfileStarredRepoModel

/**
 * Created by Kosh on 06.07.18.
 */
@Dao
abstract class UserStarredReposDao : BaseDao<ProfileStarredRepoModel>() {
    @Query("SELECT * FROM ${ProfileStarredRepoModel.TABLE_NAME} WHERE `login` = :login")
    abstract fun getStarredRepos(login: String): DataSource.Factory<Int, ProfileStarredRepoModel>

    @Query("SELECT * FROM ${ProfileStarredRepoModel.TABLE_NAME} WHERE `id` = :id")
    abstract fun getStarredRepo(id: String): LiveData<ProfileStarredRepoModel>

    @Query("DELETE FROM ${ProfileStarredRepoModel.TABLE_NAME}") abstract fun deleteAll()
    @Query("DELETE FROM ${ProfileStarredRepoModel.TABLE_NAME} WHERE `login` = :login") abstract fun deleteAll(login: String)
}