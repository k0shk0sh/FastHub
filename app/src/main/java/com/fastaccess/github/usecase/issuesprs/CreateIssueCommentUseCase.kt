package com.fastaccess.github.usecase.issuesprs

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.CommentAuthorAssociation
import com.fastaccess.data.model.CommentModel
import com.fastaccess.data.model.ShortUserModel
import com.fastaccess.data.model.TimelineModel
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.services.IssuePrService
import com.fastaccess.domain.response.body.CommentRequestModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.extension.toReactionGroup
import github.GetLastIssueCommentQuery
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 16.02.19.
 */
class CreateIssueCommentUseCase @Inject constructor(
    private val repoService: IssuePrService,
    private val apolloClient: ApolloClient,
    private val schedulerProvider: SchedulerProvider
) : BaseObservableUseCase() {

    var repo: String = ""
    var login: String = ""
    var number: Int = 0
    var body: String = ""

    override fun buildObservable(): Observable<TimelineModel> = repoService.createIssueComment(login, repo, number, CommentRequestModel(body))
        .subscribeOn(schedulerProvider.ioThread())
        .observeOn(schedulerProvider.uiThread())
        .flatMap { Rx2Apollo.from(apolloClient.query(GetLastIssueCommentQuery(login, repo, number))) }
        .map {
            val type = it.data()?.repositoryOwner?.repository?.issue?.timelineItems?.nodes?.firstOrNull()
                as? GetLastIssueCommentQuery.AsIssueComment ?: return@map TimelineModel()
            val node = type.fragments.comment ?: return@map TimelineModel()
            return@map TimelineModel(
                comment = CommentModel(
                    node.id,
                    node.databaseId,
                    ShortUserModel(node.author?.login, node.author?.login, avatarUrl = node.author?.avatarUrl.toString()),
                    node.body, CommentAuthorAssociation.fromName(node.authorAssociation.rawValue()),
                    node.reactionGroups?.map { it.fragments.reactions.toReactionGroup() }, node.createdAt, node.updatedAt,
                    node.isViewerCanReact, node.isViewerCanDelete, node.isViewerCanUpdate, node.isViewerDidAuthor, node.isViewerCanMinimize
                )
            )
        }
}