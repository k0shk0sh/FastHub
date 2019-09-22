package com.fastaccess.fasthub.reviews

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Input
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.fasthub.reviews.usecase.GetReviewUseCase
import com.fastaccess.fasthub.reviews.usecase.GetReviewsUseCase
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.editor.usecase.CreateIssueCommentUseCase
import com.fastaccess.github.editor.usecase.DeleteCommentUseCase
import com.fastaccess.github.editor.usecase.EditCommentUseCase
import javax.inject.Inject

class ReviewsViewModel @Inject constructor(
    private val timelineUseCase: GetReviewsUseCase,
    private val getReviewUseCase: GetReviewUseCase,
    private val editCommentUseCase: EditCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val createCommentUsecase: CreateIssueCommentUseCase
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null
    private val list = arrayListOf<TimelineModel>()

    val commentProgress = MutableLiveData<Boolean>()
    val timeline = MutableLiveData<ArrayList<TimelineModel>>()

    fun load(login: String, repo: String, number: Int, reload: Boolean = false) {
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

    fun load(
        id: String,
        reload: Boolean = false
    ) {
        if (reload) {
            pageInfo = null
            list.clear()
        }
        val pageInfo = pageInfo
        if (!reload && (pageInfo != null && !pageInfo.hasNextPage)) return
        val cursor = if (hasNext()) pageInfo?.endCursor else null
        getReviewUseCase.id = id
        getReviewUseCase.page = Input.optional(cursor)
        justSubscribe(getReviewUseCase.buildObservable()
            .doOnNext {
                this.pageInfo = it.first
                list.addAll(it.second)
                timeline.postValue(ArrayList(list))
            })
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
                    return@map list
                }
                .doOnNext { list ->
                    timeline.postValue(ArrayList(list))
                })
        }
    }

    fun createComment(
        login: String,
        repo: String,
        number: Int,
        comment: String
    ) {
        createCommentUsecase.login = login
        createCommentUsecase.repo = repo
        createCommentUsecase.number = number
        createCommentUsecase.body = comment
        add(createCommentUsecase.buildObservable()
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
}