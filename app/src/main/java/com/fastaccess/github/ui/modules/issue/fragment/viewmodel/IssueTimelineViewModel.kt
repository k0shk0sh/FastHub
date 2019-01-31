package com.fastaccess.github.ui.modules.issue.fragment.viewmodel

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Input
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.data.repository.IssueRepositoryProvider
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.issuesprs.GetIssueTimelineUseCase
import com.fastaccess.github.usecase.issuesprs.GetIssueUseCase
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class IssueTimelineViewModel @Inject constructor(
    private val getIssueUseCase: GetIssueUseCase,
    private val timelineUseCase: GetIssueTimelineUseCase,
    private val issueRepositoryProvider: IssueRepositoryProvider
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null
    val timeline = MutableLiveData<ArrayList<TimelineModel>>()

    override fun onCleared() {
        super.onCleared()
        getIssueUseCase.dispose()
        timelineUseCase.dispose()
    }

    fun loadIssue(login: String, repo: String, number: Int) {
        getIssueUseCase.login = login
        getIssueUseCase.repo = repo
        getIssueUseCase.number = number
        justSubscribe(getIssueUseCase.buildCompletable().doOnComplete {
            loadData(login, repo, number, true)
        })
    }

    fun getIssue(login: String, repo: String, number: Int) = issueRepositoryProvider.getIssueByNumber("$login/$repo", number)

    fun loadData(login: String, repo: String, number: Int, reload: Boolean = false) {
        if (reload) {
            pageInfo = null
            timeline.value?.clear()
        }
        val pageInfo = pageInfo
        if (!reload && (pageInfo != null && !pageInfo.hasNextPage)) return
        val cursor = if (hasNext()) pageInfo?.endCursor else null
        timelineUseCase.login = login
        timelineUseCase.repo = repo
        timelineUseCase.number = number
        timelineUseCase.page = Input.optional(cursor)
        justSubscribe(timelineUseCase.buildObservable()
            .doOnNext {
                Timber.e("${it.size}")
                timeline.postValue(ArrayList(it))
            })
    }


    fun hasNext() = pageInfo?.hasNextPage ?: false
}