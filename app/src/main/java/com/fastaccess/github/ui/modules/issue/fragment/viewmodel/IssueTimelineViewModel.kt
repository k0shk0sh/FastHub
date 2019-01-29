package com.fastaccess.github.ui.modules.issue.fragment.viewmodel

import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.repository.IssueRepositoryProvider
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.issuesprs.GetIssueUseCase
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class IssueTimelineViewModel @Inject constructor(
    private val getIssueUseCase: GetIssueUseCase,
    private val issueRepositoryProvider: IssueRepositoryProvider
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null

    override fun onCleared() {
        super.onCleared()
        getIssueUseCase.dispose()
    }

    fun loadIssue(login: String, repo: String, number: Int) {
        getIssueUseCase.login = login
        getIssueUseCase.repo = repo
        getIssueUseCase.number = number
        justSubscribe(getIssueUseCase.buildCompletable())
    }

    fun getIssue(login: String, repo: String, number: Int) = issueRepositoryProvider.getIssueByNumber("$login/$repo", number)

    fun loadData(login: String, repo: String, number: Int, reload: Boolean = false) {
        if (reload) {
            pageInfo = null
        }
        val pageInfo = pageInfo
        if (!reload && (pageInfo != null && !pageInfo.hasNextPage)) return
        val cursor = if (hasNext()) pageInfo?.endCursor else null
        // TODO
    }


    fun hasNext() = pageInfo?.hasNextPage ?: false
}