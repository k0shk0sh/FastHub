package com.fastaccess.github.editor.usecase

import com.fastaccess.data.model.TimelineType
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.response.body.CommentRequestModel
import com.fastaccess.domain.services.CommitService
import com.fastaccess.domain.services.IssuePrService
import com.fastaccess.domain.services.ReviewService
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import retrofit2.HttpException
import javax.inject.Inject

class EditCommentUseCase @Inject constructor(
    private val issueService: IssuePrService,
    private val reviewService: ReviewService,
    private val commitService: CommitService,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {
    var repo: String = ""
    var login: String = ""
    var commentId: Long = 0
    var comment: String = ""
    var number: Int = 0
    var type: TimelineType = TimelineType.ISSUE

    override fun buildObservable(): Observable<*> = when (type) {
        TimelineType.ISSUE -> issueService.editIssueComment(login, repo, commentId, CommentRequestModel(comment))
        TimelineType.REVIEW -> reviewService.editComment(login, repo, commentId, CommentRequestModel(comment))
        TimelineType.REVIEW_BODY -> reviewService.editReview(login, repo, number, commentId, CommentRequestModel(comment))
        TimelineType.COMMIT -> commitService.editCommitComment(login, repo, commentId, CommentRequestModel(comment))
        TimelineType.GIST -> TODO()
    }
        .flatMap {
            return@flatMap if (it.code() == 200 || it.code() == 204) {
                Observable.just(it)
            } else {
                Observable.error(HttpException(it))
            }
        }
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
}