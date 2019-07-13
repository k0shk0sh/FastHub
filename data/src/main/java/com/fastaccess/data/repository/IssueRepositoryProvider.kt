package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.fastaccess.data.persistence.dao.IssueDao
import com.fastaccess.data.persistence.models.IssueModel
import io.reactivex.Maybe
import javax.inject.Inject

/**
 * Created by Kosh on 27.01.19.
 */
class IssueRepositoryProvider @Inject constructor(
    private val dao: IssueDao
) : IssueRepository {
    override fun upsert(issueModel: IssueModel) = dao.upsert(issueModel)
    override fun getIssues(repo: String, state: String): DataSource.Factory<Int, IssueModel> = dao.getIssues(repo, state)
    override fun getIssueById(id: String): LiveData<IssueModel> = dao.getIssueById(id)
    override fun getIssueByNumber(repo: String, number: Int): LiveData<IssueModel?> = dao.getIssueByNumber(repo, number)
    override fun getIssueByNumberMaybe(repo: String, number: Int): Maybe<IssueModel> = dao.getIssueByNumberSingle(repo, number)
    override fun deleteAll(repo: String) = dao.deleteAll(repo)
    override fun deleteAll() = dao.deleteAll()

}

interface IssueRepository {
    fun upsert(issueModel: IssueModel)
    fun getIssues(repo: String, state: String): DataSource.Factory<Int, IssueModel>
    fun getIssueById(id: String): LiveData<IssueModel>
    fun getIssueByNumber(repo: String, number: Int): LiveData<IssueModel?>
    fun getIssueByNumberMaybe(repo: String, number: Int): Maybe<IssueModel>
    fun deleteAll(repo: String)
    fun deleteAll()
}