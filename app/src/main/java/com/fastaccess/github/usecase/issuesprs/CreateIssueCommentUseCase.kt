package com.fastaccess.github.usecase.issuesprs

import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.repository.services.IssuePrService
import com.fastaccess.domain.response.body.CommentRequestModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 16.02.19.
 */
class CreateIssueCommentUseCase @Inject constructor(
    private val repoService: IssuePrService,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var repo: String = ""
    var login: String = ""
    var number: Int = 0
    var body: String = ""

    override fun buildObservable(): Observable<Boolean> = repoService.createIssueComment(login, repo, number, CommentRequestModel(body))
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
        .map { it.isSuccessful }
}