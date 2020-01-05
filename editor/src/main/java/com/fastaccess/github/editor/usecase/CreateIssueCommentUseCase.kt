package com.fastaccess.github.editor.usecase

import com.fastaccess.data.model.*
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.response.body.CommentRequestModel
import com.fastaccess.domain.services.IssuePrService
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

    override fun buildObservable(): Observable<TimelineModel> = repoService.createIssueComment(login, repo, number, CommentRequestModel(body))
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
        .map { response ->
            val association = CommentAuthorAssociation.fromName(response.authorAssociation ?: "")
            val canAlter = association == CommentAuthorAssociation.COLLABORATOR || association == CommentAuthorAssociation.OWNER
            TimelineModel(
                comment = CommentModel(
                    response.nodeId, response.id, ShortUserModel(
                        response.user?.login, response.user?.login, response.user?.url, avatarUrl = response.user?.avatarUrl
                    ),
                    response.body, association, emptyReactionsList(), response.createdAt,
                    response.UpdatedAt, true, canAlter, canAlter, true,
                    canAlter, response.path, response.position?.toInt()
                )
            )
        }
}