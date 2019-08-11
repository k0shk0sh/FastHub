package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.fastaccess.data.persistence.dao.PullRequestDao
import com.fastaccess.data.persistence.models.PullRequestModel
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Kosh on 27.01.19.
 */
class PullRequestRepositoryProvider @Inject constructor(
    private val dao: PullRequestDao
) : PullRequestRepository {

    override fun upsert(pr: PullRequestModel) = dao.update(pr)
    override fun getPullRequests(
        repo: String,
        state: String
    ): DataSource.Factory<Int, PullRequestModel> = dao.getPullRequests(repo, state)

    override fun getPullRequestById(id: String): LiveData<PullRequestModel> = dao.getPullRequestById(id)
    override fun getPullRequestByNumber(
        repo: String,
        number: Int
    ): LiveData<PullRequestModel?> = dao.getPullRequestByNumber(repo, number)

    override fun getPullRequestByNumberMaybe(
        repo: String,
        number: Int
    ): Maybe<PullRequestModel> = dao.getPullRequestByNumberSingle(repo, number)

    override fun deleteAll(repo: String) = dao.deleteAll(repo)
    override fun deleteAll() = dao.deleteAll()

}

interface PullRequestRepository {
    fun upsert(pr: PullRequestModel)
    fun getPullRequests(
        repo: String,
        state: String
    ): DataSource.Factory<Int, PullRequestModel>

    fun getPullRequestById(id: String): LiveData<PullRequestModel>
    fun getPullRequestByNumber(
        repo: String,
        number: Int
    ): LiveData<PullRequestModel?>

    fun getPullRequestByNumberMaybe(
        repo: String,
        number: Int
    ): Maybe<PullRequestModel>

    fun deleteAll(repo: String)
    fun deleteAll()
}