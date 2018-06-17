package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import com.fastaccess.data.persistence.dao.MainIssuesPullsDao
import com.fastaccess.data.persistence.models.MainIssuesPullsModel
import javax.inject.Inject

/**
 * Created by Kosh on 17.06.18.
 */
class MainIssuesPullsRepositoryProvider @Inject constructor(private val dao: MainIssuesPullsDao) : MainIssuesPullsRepository {
    override fun getIssues(): LiveData<List<MainIssuesPullsModel>> = dao.getIssues()
    override fun getPulls(): LiveData<List<MainIssuesPullsModel>> = dao.getPulls()
    override fun insert(model: MainIssuesPullsModel): Long = dao.insert(model)
    override fun update(model: MainIssuesPullsModel): Int = dao.update(model)
    override fun delete(model: MainIssuesPullsModel) = dao.delete(model)
    override fun deleteAll() = dao.deleteAll()
    override fun deleteAllIssues() = dao.deleteAllIssues()
    override fun deleteAllPrs() = dao.deleteAllPrs()
}

interface MainIssuesPullsRepository {
    fun getIssues(): LiveData<List<MainIssuesPullsModel>>
    fun getPulls(): LiveData<List<MainIssuesPullsModel>>
    fun insert(model: MainIssuesPullsModel): Long
    fun update(model: MainIssuesPullsModel): Int
    fun delete(model: MainIssuesPullsModel)
    fun deleteAllIssues()
    fun deleteAllPrs()
    fun deleteAll()
}