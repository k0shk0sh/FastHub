package com.fastaccess.data.persistence.dao

import androidx.room.*
import com.fastaccess.data.persistence.models.MainIssuesPullsModel
import io.reactivex.Maybe

/**
 * Created by Kosh on 17.06.18.
 */
@Dao interface MainIssuesPullsDao {

    @Query("SELECT * FROM ${MainIssuesPullsModel.TABLE_NAME} WHERE `state` = '' AND `login` = :login LIMIT 5")
    fun getIssues(login: String): Maybe<List<MainIssuesPullsModel>>

    @Query("SELECT * FROM ${MainIssuesPullsModel.TABLE_NAME} WHERE `state` != '' AND `login` = :login  LIMIT 5")
    fun getPulls(login: String): Maybe<List<MainIssuesPullsModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: MainIssuesPullsModel): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(model: MainIssuesPullsModel): Int

    @Delete fun delete(model: MainIssuesPullsModel)

    @Query("DELETE FROM ${MainIssuesPullsModel.TABLE_NAME} WHERE `state` = '' ") fun deleteAllIssues()
    @Query("DELETE FROM ${MainIssuesPullsModel.TABLE_NAME} WHERE `state` != '' ") fun deleteAllPrs()

    @Query("DELETE FROM ${MainIssuesPullsModel.TABLE_NAME}") fun deleteAll()
}