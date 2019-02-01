package com.fastaccess.github.ui.modules.issue.fragment.viewmodel

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Input
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.data.repository.IssueRepositoryProvider
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.usecase.issuesprs.GetIssueTimelineUseCase
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class IssueTimelineViewModel @Inject constructor(
    private val timelineUseCase: GetIssueTimelineUseCase,
    private val issueRepositoryProvider: IssueRepositoryProvider
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null
    val timeline = MutableLiveData<ArrayList<TimelineModel>>()
    private val list = arrayListOf<TimelineModel>()

    override fun onCleared() {
        super.onCleared()
        timelineUseCase.dispose()
    }

    fun getIssue(login: String, repo: String, number: Int) = issueRepositoryProvider.getIssueByNumber("$login/$repo", number)

    fun loadData(login: String, repo: String, number: Int, reload: Boolean = false) {
        if (reload) {
            pageInfo = null
            list.clear()
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
                this.pageInfo = it.first
                list.addAll(it.second)
                timeline.postValue(ArrayList(list))
            })
    }

    fun hasNext() = pageInfo?.hasNextPage ?: false
}