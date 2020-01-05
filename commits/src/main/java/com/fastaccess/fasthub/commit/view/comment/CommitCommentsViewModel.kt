package com.fastaccess.fasthub.commit.view.comment

import androidx.lifecycle.MutableLiveData
import com.apollographql.apollo.api.Input
import com.fastaccess.data.model.CommentModel
import com.fastaccess.data.model.PageInfoModel
import com.fastaccess.data.model.TimelineType
import com.fastaccess.domain.response.ResponseWithCounterModel
import com.fastaccess.fasthub.commit.usecase.CreateCommitCommentUseCase
import com.fastaccess.fasthub.commit.usecase.GetCommitCommentsUseCase
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.editor.usecase.DeleteCommentUseCase
import com.fastaccess.github.editor.usecase.EditCommentUseCase
import com.fastaccess.github.extensions.toArrayList
import javax.inject.Inject

/**
 * Created by Kosh on 20.10.18.
 */
class CommitCommentsViewModel @Inject constructor(
    private val usecase: GetCommitCommentsUseCase,
    private val editCommentUseCase: EditCommentUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val createCommitCommentUseCase: CreateCommitCommentUseCase
) : BaseViewModel() {

    private var pageInfo: PageInfoModel? = null
    private val list = arrayListOf<CommentModel>()

    val commentProgress = MutableLiveData<Boolean>()
    val data = MutableLiveData<List<CommentModel>>()
    val commentAddedLiveData = MutableLiveData<Boolean>()

    override fun onCleared() {
        super.onCleared()
        usecase.dispose()
    }

    fun loadData(sha: String, login: String, repo: String, reload: Boolean = false) {
        if (reload) {
            this.list.clear()
            pageInfo = null
        }
        val pageInfo = pageInfo
        if (!reload && (pageInfo != null && !pageInfo.hasNextPage)) return
        val cursor = if (hasNext()) pageInfo?.endCursor else null
        usecase.page = Input.optional(cursor)
        usecase.login = login
        usecase.repo = repo
        usecase.sha = sha
        justSubscribe(usecase.buildObservable()
            .doOnNext {
                onRequestFinished(it)
            })
    }

    fun hasNext() = pageInfo?.hasNextPage ?: false

    fun deleteComment(login: String, repo: String, commentId: Long) {
        deleteCommentUseCase.commentId = commentId
        deleteCommentUseCase.login = login
        deleteCommentUseCase.repo = repo
        deleteCommentUseCase.type = TimelineType.COMMIT
        justSubscribe(deleteCommentUseCase.buildObservable()
            .map {
                val index = list.indexOfFirst { it.databaseId == commentId.toInt() }
                if (index != -1) {
                    list.removeAt(index)
                    val counter = this.counter.value ?: 0
                    postCounter(counter - 1)
                }
                return@map list
            }.doOnNext {
                data.postValue(it.toArrayList())
            })
    }

    fun editComment(login: String, repo: String, comment: String, commentId: Int) {
        editCommentUseCase.comment = comment
        editCommentUseCase.commentId = commentId.toLong()
        editCommentUseCase.repo = repo
        editCommentUseCase.login = login
        editCommentUseCase.type = TimelineType.COMMIT
        justSubscribe(deleteCommentUseCase.buildObservable()
            .map {
                val index = list.indexOfFirst { it.databaseId == commentId }
                val item = list.getOrNull(index) ?: return@map list
                item.body = comment
                list[index] = item
                return@map list
            }.doOnNext {
                data.postValue(it.toArrayList())
            })
    }

    fun addComment(login: String, repo: String, sha: String, comment: String) {
        createCommitCommentUseCase.comment = comment
        createCommitCommentUseCase.repo = repo
        createCommitCommentUseCase.login = login
        createCommitCommentUseCase.sha = sha
        add(createCommitCommentUseCase.buildObservable()
            .doOnSubscribe { commentProgress.postValue(true) }
            .doOnNext {
                commentProgress.postValue(false)
                commentAddedLiveData.postValue(true)
            }
            .subscribe({
                commentProgress.postValue(false)
            }, {
                commentProgress.postValue(false)
                handleError(it)
                it.printStackTrace()
            })
        )
    }

    private fun onRequestFinished(pair: Pair<PageInfoModel, ResponseWithCounterModel<CommentModel>>) {
        this.pageInfo = pair.first
        postCounter(pair.second.totalCount)
        this.list.addAll(pair.second.t)
        this.data.postValue(list.toArrayList()) // create new copy of list as submitList will never be notified
    }

}