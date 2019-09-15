package com.fastaccess.fasthub.commit.usecase

import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.response.body.CommentRequestModel
import com.fastaccess.domain.services.CommitService
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class CreateCommitCommentUseCase @Inject constructor(
    private val commitService: CommitService,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var login: String = ""
    var repo: String = ""
    var sha: String = ""
    var comment: String = ""

    override fun buildObservable(): Observable<*> = commitService.postCommitComment(login, repo, sha, CommentRequestModel(comment))
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
}