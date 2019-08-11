package com.fastaccess.data.persistence.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.fastaccess.data.persistence.models.PullRequestModel
import io.reactivex.Maybe

/**
 * Created by Kosh on 06.07.18.
 */
@Dao
abstract class PullRequestDao : BaseDao<PullRequestModel>() {
    @Query("SELECT * FROM ${PullRequestModel.TABLE_NAME} WHERE `repo_nameWithOwner` = :repo AND `state` = :state")
    abstract fun getPullRequests(repo: String, state: String): DataSource.Factory<Int, PullRequestModel>

    @Query("SELECT * FROM ${PullRequestModel.TABLE_NAME}  WHERE `id` = :id")
    abstract fun getPullRequestById(id: String): LiveData<PullRequestModel>

    @Query("SELECT * FROM ${PullRequestModel.TABLE_NAME} WHERE `repo_nameWithOwner` = :repo AND `number` = :number")
    abstract fun getPullRequestByNumber(repo: String, number: Int): LiveData<PullRequestModel?>

    @Query("SELECT * FROM ${PullRequestModel.TABLE_NAME} WHERE `repo_nameWithOwner` = :repo AND `number` = :number")
    abstract fun getPullRequestByNumberSingle(repo: String, number: Int): Maybe<PullRequestModel>

    @Query("DELETE FROM ${PullRequestModel.TABLE_NAME}  WHERE `repo_nameWithOwner` = :repo") abstract fun deleteAll(repo: String)

    @Query("DELETE FROM ${PullRequestModel.TABLE_NAME}") abstract fun deleteAll()
}