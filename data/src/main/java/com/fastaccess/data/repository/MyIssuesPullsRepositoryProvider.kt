package com.fastaccess.data.repository

import androidx.lifecycle.LiveData
import com.fastaccess.data.persistence.dao.MyIssuesPullsDao
import com.fastaccess.data.persistence.models.MyIssuesPullsModel
import javax.inject.Inject

/**
 * Created by Kosh on 17.06.18.
 */
class MyIssuesPullsRepositoryProvider @Inject constructor(
    private val dao: MyIssuesPullsDao
) : MyIssuesPullsRepository {
    override fun getMainScreenIssues(): LiveData<List<MyIssuesPullsModel>> = dao.getMainScreenIssues()
    override fun getMainScreenPulls(): LiveData<List<MyIssuesPullsModel>> = dao.getMainScreenPulls()
    override fun insert(model: MyIssuesPullsModel): Long = dao.insert(model)
    override fun insert(model: List<MyIssuesPullsModel>) = dao.insert(model)
    override fun update(model: MyIssuesPullsModel): Int = dao.update(model)
    override fun delete(model: MyIssuesPullsModel) = dao.delete(model)
    override fun deleteAll() = dao.deleteAll()
    override fun deleteAllIssues() = dao.deleteAllIssues()
    override fun deleteAllPrs() = dao.deleteAllPrs()
}

interface MyIssuesPullsRepository {
    fun getMainScreenIssues(): LiveData<List<MyIssuesPullsModel>>
    fun getMainScreenPulls(): LiveData<List<MyIssuesPullsModel>>
    fun insert(model: MyIssuesPullsModel): Long
    fun insert(model: List<MyIssuesPullsModel>)
    fun update(model: MyIssuesPullsModel): Int
    fun delete(model: MyIssuesPullsModel)
    fun deleteAllIssues()
    fun deleteAllPrs()
    fun deleteAll()
}