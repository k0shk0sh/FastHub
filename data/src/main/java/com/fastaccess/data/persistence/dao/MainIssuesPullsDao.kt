package com.fastaccess.data.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fastaccess.data.persistence.models.MainIssuesPullsModel

/**
 * Created by Kosh on 17.06.18.
 */
@Dao interface MainIssuesPullsDao {

    @Query("SELECT * FROM ${MainIssuesPullsModel.TABLE_NAME} WHERE `state` = '' LIMIT 5")
    fun getIssues(): LiveData<List<MainIssuesPullsModel>>

    @Query("SELECT * FROM ${MainIssuesPullsModel.TABLE_NAME} WHERE `state` != '' LIMIT 5")
    fun getPulls(): LiveData<List<MainIssuesPullsModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: MainIssuesPullsModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: List<MainIssuesPullsModel>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(model: MainIssuesPullsModel): Int

    @Delete fun delete(model: MainIssuesPullsModel)
    @Query("DELETE FROM ${MainIssuesPullsModel.TABLE_NAME} WHERE `state` = '' ") fun deleteAllIssues()
    @Query("DELETE FROM ${MainIssuesPullsModel.TABLE_NAME} WHERE `state` != '' ") fun deleteAllPrs()
    @Query("DELETE FROM ${MainIssuesPullsModel.TABLE_NAME}") fun deleteAll()
}