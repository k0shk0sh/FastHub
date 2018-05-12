package com.fastaccess.data.persistence.dao

import android.arch.persistence.room.*
import com.fastaccess.data.persistence.models.LoginModel
import com.fastaccess.data.persistence.models.LoginModel.Companion.TABLE_NAME
import io.reactivex.Maybe

/**
 * Created by Kosh on 11.05.18.
 */
@Dao interface LoginDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE `isLoggedIn` = 1 LIMIT 1") fun getLogin(): Maybe<LoginModel?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(login: LoginModel): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(login: LoginModel): Int

    @Delete fun deleteLogin(login: LoginModel)
}