package com.fastaccess.github.ui.modules.issue.fragment.viewmodel

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Input
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.data.repository.IssueRepository
import com.fastaccess.data.repository.LoginRepository
import com.fastaccess.github.editor.usecase.CreateIssueCommentUseCase
import com.fastaccess.github.editor.usecase.DeleteCommentUseCase
import com.fastaccess.github.editor.usecase.EditCommentUseCase
import com.fastaccess.github.extensions.filterNull
import com.fastaccess.github.extensions.map
import com.fastaccess.github.usecase.issuesprs.*
import github.type.LockReason
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class IssueTimelineViewModel @Inject constructor(
    private val issueUseCase: GetIssueUseCase,
    private val timelineUseCase: GetIssueTimelineUseCase,
    private val issueRepositoryProvider: IssueRepository,
    private val closeOpenIssuePrUseCase: CloseOpenIssuePrUseCase,
    private val lockUnlockIssuePrUseCase: LockUnlockIssuePrUseCase,
    private val loginRepositoryProvider: LoginRepository,
    private val createIssueCommentUseCase: CreateIssueCommentUseCase,
    private val editIssuePrUseCase: EditIssuePrUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val editCommentUseCase: EditCommentUseCase
) : com.fastaccess.github.base.BaseViewModel() {

    private var pageInfo: PageInfoModel? = null
    private val list = arrayListOf<TimelineModel>()
    val forceAdapterUpdate = MutableLiveData<Boolean>()
    val timeline = MutableLiveData<ArrayList<TimelineModel>>()
    val userNamesLiveData = MutableLiveData<ArrayList<String>>()
    val commentProgress = MutableLiveData<Boolean>()

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
        closeOpenIssuePrUseCase.repo = repo
        closeOpenIssuePrUseCase.login = login
        closeOpenIssuePrUseCase.number = number
        justSubscribe(closeOpenIssuePrUseCase.buildObservable()
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
        add(createIssueCommentUseCase.buildObservable()
            .doOnSubscribe { commentProgress.postValue(true) }
            .subscribe({
                addTimeline(it)
                commentProgress.postValue(false)
            }, {
                commentProgress.postValue(false)
                handleError(it)
            })
        )
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

    fun editIssue(
        login: String,
        repo: String,
        number: Int,
        title: String?,
        description: String?
    ) {
        editIssuePrUseCase.login = login
        editIssuePrUseCase.repo = repo
        editIssuePrUseCase.number = number
        editIssuePrUseCase.title = title
        editIssuePrUseCase.description = description
        justSubscribe(editIssuePrUseCase.buildObservable())
    }

    fun deleteComment(
        login: String,
        repo: String,
        commentId: Long
    ) {
        deleteCommentUseCase.commentId = commentId
        deleteCommentUseCase.login = login
        deleteCommentUseCase.repo = repo
        justSubscribe(deleteCommentUseCase.buildObservable()
            .map {
                val index = list.indexOfFirst { it.comment?.databaseId?.toLong() == commentId }
                if (index != -1) {
                    list.removeAt(index)
                }
                return@map list
            }
            .doOnNext { list ->
                timeline.postValue(ArrayList(list))
            })
    }

    fun editComment(
        login: String,
        repo: String,
        comment: String?,
        commentId: Long?
    ) {
        if (!comment.isNullOrBlank() && commentId != null) {
            editCommentUseCase.comment = comment
            editCommentUseCase.login = login
            editCommentUseCase.repo = repo
            editCommentUseCase.commentId = commentId
            justSubscribe(editCommentUseCase.buildObservable()
                .map {
                    val index = list.indexOfFirst { it.comment?.databaseId?.toLong() == commentId }
                    val item = list.getOrNull(index) ?: return@map list
                    item.comment?.body = comment
                    list[index] = item
                    Timber.e("${list[index]}")
                    return@map list
                }
                .doOnNext { list ->
                    timeline.postValue(ArrayList(list))
                    forceAdapterUpdate.postValue(true)
                })
        }
    }
}