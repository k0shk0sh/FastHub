package com.fastaccess.github.ui.modules.pr.fragment.viewmodel

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Input
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.data.model.TimelineType
import com.fastaccess.data.repository.LoginRepository
import com.fastaccess.data.repository.PullRequestRepository
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.editor.usecase.CreateIssueCommentUseCase
import com.fastaccess.github.editor.usecase.DeleteCommentUseCase
import com.fastaccess.github.editor.usecase.EditCommentUseCase
import com.fastaccess.github.extensions.filterNull
import com.fastaccess.github.extensions.map
import com.fastaccess.github.extensions.toArrayList
import com.fastaccess.github.usecase.issuesprs.*
import github.type.LockReason
import io.reactivex.Maybe
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class PullRequestTimelineViewModel @Inject constructor(
    private val issueUseCase: GetPullRequestUseCase,
    private val timelineUseCase: GetPullRequestTimelineUseCase,
    private val issueRepositoryProvider: PullRequestRepository,
    private val closeOpenIssuePrUseCase: CloseOpenIssuePrUseCase,
    private val lockUnlockIssuePrUseCase: LockUnlockIssuePrUseCase,
    private val loginRepositoryProvider: LoginRepository,
    private val createIssueCommentUseCase: CreateIssueCommentUseCase,
    private val editIssuePrUseCase: EditIssuePrUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val editCommentUseCase: EditCommentUseCase,
    private val schedulerProvider: SchedulerProvider
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null
    private val list = arrayListOf<TimelineModel>()
    val forceAdapterUpdate = MutableLiveData<Boolean>()
    val timeline = MutableLiveData<ArrayList<TimelineModel>>()
    val userNamesLiveData = MutableLiveData<ArrayList<String>>()
    val commentProgress = MutableLiveData<Boolean>()

    fun getPullRequest(
        login: String,
        repo: String,
        number: Int
    ) = issueRepositoryProvider.getPullRequestByNumber("$login/$repo", number)
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
    ): Observable<Pair<PageInfoModel, ArrayList<TimelineModel>>> {
        timelineUseCase.login = login
        timelineUseCase.repo = repo
        timelineUseCase.number = number
        timelineUseCase.page = Input.optional(cursor)
        return timelineUseCase.buildObservable()
            .doOnNext {
                this.pageInfo = it.first
                list.addAll(it.second)
                timeline.postValue(list.toArrayList())
            }
    }

    fun closeOpenIssue(
        login: String,
        repo: String,
        number: Int
    ) {
        closeOpenIssuePrUseCase.isPr = true
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
        isLock: Boolean
    ) {
        lockUnlockIssuePrUseCase.repo = repo
        lockUnlockIssuePrUseCase.login = login
        lockUnlockIssuePrUseCase.number = number
        lockUnlockIssuePrUseCase.lockReason = lockReason
        lockUnlockIssuePrUseCase.lock = isLock
        lockUnlockIssuePrUseCase.isPr = true
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
        timeline.postValue(list.toArrayList())
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
        editIssuePrUseCase.isPr = true
        justSubscribe(editIssuePrUseCase.buildObservable())
    }

    fun deleteComment(
        login: String,
        repo: String,
        commentId: Long,
        type: TimelineType = TimelineType.ISSUE,
        number: Int,
        msg: String? = null
    ) {
        deleteCommentUseCase.commentId = commentId
        deleteCommentUseCase.login = login
        deleteCommentUseCase.repo = repo
        deleteCommentUseCase.type = type
        deleteCommentUseCase.number = number
        deleteCommentUseCase.msg = msg
        justSubscribe(deleteCommentUseCase.buildObservable()
            .map {
                val index = getIndexOfComment(type, commentId)
                if (index != -1) {
                    if (type == TimelineType.REVIEW) {
                        val item = list.getOrNull(index) ?: return@map list
                        item.review?.comment = null
                        list[index] = item
                    } else {
                        list.removeAt(index)
                    }
                }
                return@map list
            }
            .doOnNext { list ->
                timeline.postValue(list.toArrayList())
            })
    }

    fun editComment(
        login: String,
        repo: String,
        comment: String?,
        commentId: Long?,
        type: TimelineType = TimelineType.ISSUE,
        number: Int
    ) {
        if (!comment.isNullOrBlank() && commentId != null) {
            editCommentUseCase.comment = comment
            editCommentUseCase.login = login
            editCommentUseCase.repo = repo
            editCommentUseCase.commentId = commentId
            editCommentUseCase.type = type
            editCommentUseCase.number = number
            justSubscribe(editCommentUseCase.buildObservable()
                .map {
                    val index = getIndexOfComment(type, commentId)
                    val item = list.getOrNull(index) ?: return@map list
                    when (type) {
                        TimelineType.ISSUE -> item.comment?.body = comment
                        TimelineType.REVIEW -> item.review?.comment?.body = comment
                        TimelineType.REVIEW_BODY -> item.review?.body = comment
                        TimelineType.COMMIT -> item.commitThread?.comment?.body = comment
                        else -> {
                        }
                    }
                    list[index] = item
                    return@map list
                }
                .doOnNext { list ->
                    timeline.postValue(list.toArrayList())
                    forceAdapterUpdate.postValue(true)
                })
        }
    }

    fun hasCommentableReviews(): Maybe<Boolean> = Maybe.create<Boolean> { emitter ->
        emitter.onSuccess(list.any { timelineModel ->
            return@any timelineModel.review?.comment?.path != null
        })
        emitter.onComplete()
    }.subscribeOn(schedulerProvider.ioThread()).observeOn(schedulerProvider.uiThread())

    private fun getIndexOfComment(type: TimelineType, commentId: Long?): Int = list.indexOfFirst {
        return@indexOfFirst when (type) {
            TimelineType.ISSUE -> it.comment?.databaseId?.toLong() == commentId
            TimelineType.REVIEW_BODY -> it.review?.databaseId?.toLong() == commentId
            TimelineType.REVIEW -> it.review?.comment?.databaseId?.toLong() == commentId
            TimelineType.COMMIT -> it.commitThread?.comment?.databaseId?.toLong() == commentId
            else -> false
        }
    }
}