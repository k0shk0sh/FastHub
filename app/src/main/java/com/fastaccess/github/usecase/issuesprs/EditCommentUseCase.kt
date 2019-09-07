package com.fastaccess.github.usecase.issuesprs

import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.repository.services.CommitService
import com.fastaccess.domain.repository.services.IssuePrService
import com.fastaccess.domain.repository.services.ReviewService
import com.fastaccess.domain.response.body.CommentRequestModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
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
    var type: TimelineType = TimelineType.ISSUE

    override fun buildObservable(): Observable<*> = when (type) {
        TimelineType.ISSUE -> issueService.editIssueComment(login, repo, commentId, CommentRequestModel(comment))
        TimelineType.REVIEW -> reviewService.editComment(login, repo, commentId, CommentRequestModel(comment))
        TimelineType.COMMIT -> commitService.editCommitComment(login, repo, commentId, CommentRequestModel(comment))
        TimelineType.GIST -> TODO()
    }
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
}