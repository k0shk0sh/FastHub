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
import com.fastaccess.github.usecase.issuesprs.*
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
    private val loginRepositoryProvider: LoginRepositoryProvider,
    private val createIssueCommentUseCase: CreateIssueCommentUseCase
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null
    val timeline = MutableLiveData<ArrayList<TimelineModel>>()
    private val list = arrayListOf<TimelineModel>()
    val userNamesLiveData = MutableLiveData<ArrayList<String>>()
    val commentProgress = MutableLiveData<Boolean>()

    override fun onCleared() {
        super.onCleared()
        timelineUseCase.dispose()
        editIssuePrUseCase.dispose()
        issueUseCase.dispose()
        lockUnlockIssuePrUseCase.dispose()
        createIssueCommentUseCase.dispose()
    }

    fun getIssue(
        login: String,
        repo: String,
        number: Int
    ) = issueRepositoryProvider.getIssueByNumber("$login/$repo", number)
        .filterNull()
        .map { Pair(it, loginRepositoryProvider.getLoginBlocking()) }

    fun loadData(
        login: String,
        repo: String,
        number: Int,
        reload: Boolean = false
    ) {
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
                .flatMap { loadTimeline(login, repo, number, cursor) }
                .map { mapToUserNames(it.second) })
        } else {
            justSubscribe(loadTimeline(login, repo, number, cursor)
                .map { mapToUserNames(it.second) })
        }
    }

    private fun loadTimeline(
        login: String,
        repo: String,
        number: Int,
        cursor: String?
    ): Observable<Pair<PageInfoModel, List<TimelineModel>>> {
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

    fun closeOpenIssue(
        login: String,
        repo: String,
        number: Int
    ) {
        editIssuePrUseCase.repo = repo
        editIssuePrUseCase.login = login
        editIssuePrUseCase.number = number
        justSubscribe(editIssuePrUseCase.buildObservable()
            .doOnNext {
                addTimeline(it)
            })
    }

    fun lockUnlockIssue(
        login: String,
        repo: String,
        number: Int,
        lockReason: LockReason? = null,
        lock: Boolean = false
    ) {
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

    fun createComment(
        login: String,
        repo: String,
        number: Int,
        comment: String
    ) {
        createIssueCommentUseCase.login = login
        createIssueCommentUseCase.repo = repo
        createIssueCommentUseCase.number = number
        createIssueCommentUseCase.body = comment
        justSubscribe(createIssueCommentUseCase.buildObservable()
            .doOnSubscribe { commentProgress.postValue(true) }
            .doOnNext { commentProgress.postValue(false) }) // TODO(to call graphql with orderby to get latest comment
    }

    fun addTimeline(it: TimelineModel) {
        list.add(it)
        timeline.postValue(ArrayList(list))
    }

    fun hasNext() = pageInfo?.hasNextPage ?: false

    private fun mapToUserNames(list: List<TimelineModel>) {
        val _list = userNamesLiveData.value ?: arrayListOf()
        _list.addAll(list.map { it.comment?.author?.login ?: it.comment?.author?.name ?: "" })
        userNamesLiveData.postValue(_list)
    }
}