package com.fastaccess.data.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.fastaccess.data.persistence.models.ProfileRepoModel

/**
 * Created by Kosh on 06.07.18.
 */
@Dao
abstract class UserReposDao : BaseDao<ProfileRepoModel>() {
    @Query("SELECT * FROM ${ProfileRepoModel.TABLE_NAME}")
    abstract fun getRepos(): LiveData<List<ProfileRepoModel>>

    @Query("SELECT * FROM ${ProfileRepoModel.TABLE_NAME} WHERE `id` = :id")
    abstract fun getRepo(id: Long): LiveData<ProfileRepoModel>

    @Query("DELETE FROM ${ProfileRepoModel.TABLE_NAME}") abstract fun deleteAll()
}