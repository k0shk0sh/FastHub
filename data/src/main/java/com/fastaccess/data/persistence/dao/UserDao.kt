package com.fastaccess.data.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.fastaccess.data.persistence.models.UserModel

/**
 * Created by Kosh on 06.07.18.
 */
@Dao
abstract class UserDao : BaseDao<UserModel>() {
    @Query("SELECT * FROM ${UserModel.TABLE_NAME}")
    abstract fun getUsers(): LiveData<List<UserModel>>

    @Query("SELECT * FROM ${UserModel.TABLE_NAME} WHERE `login` = :login")
    abstract fun getUser(login: String): LiveData<UserModel>

    @Query("SELECT * FROM ${UserModel.TABLE_NAME} WHERE `login` = :login")
    abstract fun getUserBlocking(login: String): UserModel?

    @Query("DELETE FROM ${UserModel.TABLE_NAME}") abstract fun deleteAll()
}