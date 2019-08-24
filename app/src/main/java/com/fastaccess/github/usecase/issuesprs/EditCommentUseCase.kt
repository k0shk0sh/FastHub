package com.fastaccess.github.usecase.issuesprs

import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.repository.services.IssuePrService
import com.fastaccess.domain.response.body.CommentRequestModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class EditCommentUseCase @Inject constructor(
    private val issueService: IssuePrService,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {
    var repo: String = ""
    var login: String = ""
    var commentId: Long = 0
    var comment: String = ""
    var type: Type = Type.ISSUE

    override fun buildObservable(): Observable<*> = when (type) {
        Type.ISSUE -> issueService.editIssueComment(login, repo, commentId, CommentRequestModel(comment))
        Type.PR -> TODO()
        Type.GIST -> TODO()
        Type.COMMIT -> TODO()
    }
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())

    enum class Type {
        ISSUE, PR, GIST, COMMIT
    }
}