package com.fastaccess.github.ui.modules.issue.fragment.viewmodel

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Input
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.data.repository.IssueRepositoryProvider
import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.filterNull
import com.fastaccess.github.extensions.map
import com.fastaccess.github.usecase.issuesprs.CloseOpenIssuePrUseCase
import com.fastaccess.github.usecase.issuesprs.GetIssueTimelineUseCase
import com.fastaccess.github.usecase.issuesprs.GetIssueUseCase
import com.fastaccess.github.usecase.issuesprs.LockUnlockIssuePrUseCase
import github.type.LockReason
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class IssueTimelineViewModel @Inject constructor(
    private val issueUseCase: GetIssueUseCase,
    private val timelineUseCase: GetIssueTimelineUseCase,
    private val issueRepositoryProvider: IssueRepositoryProvider,
    private val editIssuePrUseCase: CloseOpenIssuePrUseCase,
    private val lockUnlockIssuePrUseCase: LockUnlockIssuePrUseCase,
    private val loginRepositoryProvider: LoginRepositoryProvider
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null
    val timeline = MutableLiveData<ArrayList<TimelineModel>>()
    private val list = arrayListOf<TimelineModel>()

    override fun onCleared() {
        super.onCleared()
        timelineUseCase.dispose()
        editIssuePrUseCase.dispose()
        issueUseCase.dispose()
        lockUnlockIssuePrUseCase.dispose()
    }

    fun getIssue(login: String, repo: String, number: Int) = issueRepositoryProvider.getIssueByNumber("$login/$repo", number)
        .filterNull()
        .map { Pair(it, loginRepositoryProvider.getLoginBlocking()) }

    fun loadData(login: String, repo: String, number: Int, reload: Boolean = false) {
        if (reload) {
            pageInfo = null
            list.clear()
        }
        val pageInfo = pageInfo
        if (!reload && (pageInfo != null && !pageInfo.hasNextPage)) return
        val cursor = if (hasNext()) pageInfo?.endCursor else null
        if (pageInfo == null) {
            issueUseCase.login = login
            issueUseCase.repo = repo
            issueUseCase.number = number
            justSubscribe(issueUseCase.buildObservable()
                .flatMap { loadTimeline(login, repo, number, cursor) })
        } else {
            justSubscribe(loadTimeline(login, repo, number, cursor))
        }
    }

    private fun loadTimeline(login: String, repo: String, number: Int, cursor: String?): Observable<Pair<PageInfoModel, List<TimelineModel>>> {
        timelineUseCase.login = login
        timelineUseCase.repo = repo
        timelineUseCase.number = number
        timelineUseCase.page = Input.optional(cursor)
        return timelineUseCase.buildObservable()
            .doOnNext {
                this.pageInfo = it.first
                list.addAll(it.second)
                timeline.postValue(ArrayList(list))
            }
    }

    fun closeOpenIssue(login: String, repo: String, number: Int) {
        editIssuePrUseCase.repo = repo
        editIssuePrUseCase.login = login
        editIssuePrUseCase.number = number
        justSubscribe(editIssuePrUseCase.buildObservable()
            .doOnNext {
                addTimeline(it)
            })
    }

    fun lockUnlockIssue(login: String, repo: String, number: Int, lockReason: LockReason? = null, lock: Boolean = false) {
        lockUnlockIssuePrUseCase.repo = repo
        lockUnlockIssuePrUseCase.login = login
        lockUnlockIssuePrUseCase.number = number
        lockUnlockIssuePrUseCase.lockReason = lockReason
        lockUnlockIssuePrUseCase.lock = lock
        justSubscribe(lockUnlockIssuePrUseCase.buildObservable()
            .doOnNext {
                addTimeline(it)
            })
    }

    fun addTimeline(it: TimelineModel) {
        list.add(it)
        timeline.postValue(ArrayList(list))
    }

    fun hasNext() = pageInfo?.hasNextPage ?: false
}