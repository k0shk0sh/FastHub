package com.fastaccess.github.usecase.issuesprs

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.CountModel
import com.fastaccess.data.model.EmbeddedRepoModel
import com.fastaccess.data.model.ShortUserModel
import com.fastaccess.data.persistence.models.PullRequestModel
import com.fastaccess.data.repository.PullRequestRepository
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.extension.toLabels
import com.fastaccess.extension.toMilestone
import com.fastaccess.extension.toReactionGroup
import com.fastaccess.extension.toUser
import github.GetPullRequestQuery
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 27.01.19.
 */
class GetPullRequestUseCase @Inject constructor(
    private val repoProvider: PullRequestRepository,
    private val apolloClient: ApolloClient,
    private val schedulerProvider: SchedulerProvider
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

        return Rx2Apollo.from(apolloClient.query(GetPullRequestQuery(login, repo, number)))
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
            .map { it.data()?.repositoryOwner?.repository?.pullRequest?.fragments?.fullPullRequest }
            .map { issue ->
                repoProvider.upsert(PullRequestModel(issue.id, issue.databaseId, issue.number, issue.activeLockReason?.rawValue(),
                    issue.body, issue.bodyHTML.toString(), issue.closedAt, issue.createdAt, issue.updatedAt, issue.state.rawValue(),
                    issue.title, issue.viewerSubscription?.rawValue(), ShortUserModel(
                        issue.author?.login, issue.author?.login,
                        issue.author?.url?.toString(), avatarUrl = issue.author?.avatarUrl?.toString()
                    ),
                    EmbeddedRepoModel(issue.repository.nameWithOwner), ShortUserModel(
                        issue.mergedBy?.login, issue.mergedBy?.login,
                        issue.mergedBy?.url?.toString(), avatarUrl = issue.mergedBy?.avatarUrl?.toString()
                    ),
                    CountModel(issue.userContentEdits?.totalCount),
                    issue.reactionGroups?.map { it.fragments.reactions.toReactionGroup() },
                    issue.viewerCannotUpdateReasons.map { it.rawValue() }, issue.isClosed, issue.isCreatedViaEmail, issue.isLocked,
                    issue.isViewerCanReact, issue.isViewerCanSubscribe, issue.isViewerCanUpdate, issue.isViewerDidAuthor,
                    issue.mergeable.rawValue(), issue.isMerged, issue.mergedAt,
                    issue.authorAssociation.rawValue(), issue.url.toString(), issue.labels?.nodes?.map { it.fragments.labels.toLabels() },
                    issue.milestone?.toMilestone(), issue.assignees.nodes?.map { it.fragments }?.map { it.shortUserRowItem.toUser() })
                )
            }
    }
}