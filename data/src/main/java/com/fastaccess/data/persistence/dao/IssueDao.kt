package com.fastaccess.data.persistence.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.fastaccess.data.persistence.models.IssueModel
import io.reactivex.Maybe

/**
 * Created by Kosh on 06.07.18.
 */
@Dao
abstract class IssueDao : BaseDao<IssueModel>() {
    @Query("SELECT * FROM ${IssueModel.TABLE_NAME} WHERE `repo_nameWithOwner` = :repo AND `state` = :state")
    abstract fun getIssues(repo: String, state: String): DataSource.Factory<Int, IssueModel>

    @Query("SELECT * FROM ${IssueModel.TABLE_NAME}  WHERE `id` = :id")
    abstract fun getIssueById(id: String): LiveData<IssueModel>

    @Query("SELECT * FROM ${IssueModel.TABLE_NAME} WHERE `repo_nameWithOwner` = :repo AND `number` = :number")
    abstract fun getIssueByNumber(repo: String, number: Int): LiveData<IssueModel?>

    @Query("SELECT * FROM ${IssueModel.TABLE_NAME} WHERE `repo_nameWithOwner` = :repo AND `number` = :number")
    abstract fun getIssueByNumberSingle(repo: String, number: Int): Maybe<IssueModel>

    @Query("DELETE FROM ${IssueModel.TABLE_NAME}  WHERE `repo_nameWithOwner` = :repo") abstract fun deleteAll(repo: String)

    @Query("DELETE FROM ${IssueModel.TABLE_NAME}") abstract fun deleteAll()
}