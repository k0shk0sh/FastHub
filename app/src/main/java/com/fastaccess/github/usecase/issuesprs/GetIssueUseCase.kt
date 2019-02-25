package com.fastaccess.github.usecase.issuesprs

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.CountModel
import com.fastaccess.data.model.EmbeddedRepoModel
import com.fastaccess.data.model.ShortUserModel
import com.fastaccess.data.persistence.models.IssueModel
import com.fastaccess.data.repository.IssueRepositoryProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.extension.toLabels
import com.fastaccess.extension.toMilestone
import com.fastaccess.extension.toReactionGroup
import com.fastaccess.extension.toUser
import github.GetIssueQuery
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 27.01.19.
 */
class GetIssueUseCase @Inject constructor(
    private val issueRepositoryProvider: IssueRepositoryProvider,
    private val apolloClient: ApolloClient
) : BaseObservableUseCase() {

    var login: String? = null
    var repo: String? = null
    var number: Int? = null

    override fun buildObservable(): Observable<*> {
        val login = login
        val repo = repo
        val number = number

        if (login.isNullOrEmpty() || repo.isNullOrEmpty() || number == null) {
            return Observable.error<Any>(Throwable("this should never happen ;)"))
        }

        return Rx2Apollo.from(apolloClient.query(GetIssueQuery(login, repo, number)))
            .map { it.data()?.repositoryOwner?.repository?.issue?.fragments?.fullIssue }
            .map { issue ->
                issueRepositoryProvider.upsert(IssueModel(issue.id, issue.databaseId, issue.number, issue.activeLockReason?.rawValue(),
                    issue.body, issue.bodyHTML.toString(), issue.closedAt, issue.createdAt, issue.updatedAt, issue.state.rawValue(),
                    issue.title, issue.viewerSubscription?.rawValue(), ShortUserModel(issue.author?.login, issue.author?.login,
                    issue.author?.url?.toString(), avatarUrl = issue.author?.avatarUrl?.toString()),
                    EmbeddedRepoModel(issue.repository.nameWithOwner), CountModel(issue.userContentEdits?.totalCount),
                    issue.reactionGroups?.map { it.fragments.reactions.toReactionGroup() },
                    issue.viewerCannotUpdateReasons.map { it.rawValue() }, issue.isClosed, issue.isCreatedViaEmail, issue.isLocked,
                    issue.isViewerCanReact, issue.isViewerCanSubscribe, issue.isViewerCanUpdate, issue.isViewerDidAuthor,
                    issue.authorAssociation.rawValue(), issue.url.toString(), issue.labels.toLabels(),
                    issue.milestone?.toMilestone(), issue.assignees.nodes?.map { it.fragments }?.map { it.shortUserRowItem.toUser() }))
            }
    }
}