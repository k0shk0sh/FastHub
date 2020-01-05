package com.fastaccess.data.persistence.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.fastaccess.data.persistence.models.ProfileGistModel

/**
 * Created by Kosh on 06.07.18.
 */
@Dao
abstract class UserGistsDao : BaseDao<ProfileGistModel>() {
    @Query("SELECT * FROM ${ProfileGistModel.TABLE_NAME} WHERE `login` = :login")
    abstract fun getGists(login: String): DataSource.Factory<Int, ProfileGistModel>

    @Query("SELECT * FROM ${ProfileGistModel.TABLE_NAME} WHERE `id` = :id")
    abstract fun getGist(id: String): LiveData<ProfileGistModel>

    @Query("DELETE FROM ${ProfileGistModel.TABLE_NAME}") abstract fun deleteAll()
    @Query("DELETE FROM ${ProfileGistModel.TABLE_NAME} WHERE `login` = :login") abstract fun deleteAll(login: String)
}