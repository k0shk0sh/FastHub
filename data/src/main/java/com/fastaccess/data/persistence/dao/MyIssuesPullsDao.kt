package com.fastaccess.data.persistence.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fastaccess.data.persistence.models.MyIssuesPullsModel

/**
 * Created by Kosh on 17.06.18.
 */
@Dao interface MyIssuesPullsDao {

    @Query("SELECT * FROM ${MyIssuesPullsModel.TABLE_NAME} WHERE `isPr` == 0 LIMIT 5")
    fun getMainScreenIssues(): LiveData<List<MyIssuesPullsModel>>

    @Query("SELECT * FROM ${MyIssuesPullsModel.TABLE_NAME} WHERE `isPr` == 1 LIMIT 5")
    fun getMainScreenPulls(): LiveData<List<MyIssuesPullsModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: MyIssuesPullsModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: List<MyIssuesPullsModel>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(model: MyIssuesPullsModel): Int

    @Delete fun delete(model: MyIssuesPullsModel)
    @Query("DELETE FROM ${MyIssuesPullsModel.TABLE_NAME} WHERE `state` == 0 ") fun deleteAllIssues()
    @Query("DELETE FROM ${MyIssuesPullsModel.TABLE_NAME} WHERE `state` == 1 ") fun deleteAllPrs()
    @Query("DELETE FROM ${MyIssuesPullsModel.TABLE_NAME}") fun deleteAll()
}