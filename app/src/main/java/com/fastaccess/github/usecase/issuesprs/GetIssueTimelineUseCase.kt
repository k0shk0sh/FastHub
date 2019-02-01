package com.fastaccess.github.usecase.issuesprs

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.*
import com.fastaccess.data.persistence.models.IssueModel
import com.fastaccess.data.repository.IssueRepositoryProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import github.GetIssueTimelineQuery
import github.GetIssueTimelineQuery.*
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 27.01.19.
 */
class GetIssueTimelineUseCase @Inject constructor(
    private val issueRepositoryProvider: IssueRepositoryProvider,
    private val apolloClient: ApolloClient
) : BaseObservableUseCase() {

    var login: String? = null
    var repo: String? = null
    var number: Int? = null
    var page = Input.absent<String>()

    override fun buildObservable(): Observable<Pair<PageInfoModel, List<TimelineModel>>> {
        val login = login
        val repo = repo
        val number = number

        if (login.isNullOrEmpty() || repo.isNullOrEmpty() || number == null) {
            return Observable.error(Throwable("this should never happen ;)"))
        }

        return Rx2Apollo.from(apolloClient.query(GetIssueTimelineQuery(login, repo, number, page)))
            .map { it.data()?.repositoryOwner?.repository?.issue }
            .map {
                val list = arrayListOf<TimelineModel>()
                val issue = it.fragments.fullIssue
                val issueModel = IssueModel(issue.id, issue.databaseId, issue.number, issue.activeLockReason?.rawValue(),
                    issue.body, issue.bodyHTML.toString(), issue.closedAt, issue.createdAt, issue.updatedAt, issue.state.rawValue(),
                    issue.title, issue.viewerSubscription?.rawValue(), ShortUserModel(issue.author?.login, issue.author?.login,
                    issue.author?.url?.toString(), avatarUrl = issue.author?.avatarUrl?.toString()),
                    EmbeddedRepoModel(issue.repository.nameWithOwner), CountModel(issue.userContentEdits?.totalCount), issue.reactionGroups
                    ?.map { ReactionGroupModel(it.content.rawValue(), it.createdAt, CountModel(it.users.totalCount), it.isViewerHasReacted) },
                    issue.viewerCannotUpdateReasons.map { it.rawValue() }, issue.isClosed, issue.isCreatedViaEmail, issue.isLocked,
                    issue.isViewerCanReact, issue.isViewerCanSubscribe, issue.isViewerCanUpdate, issue.isViewerDidAuthor)
                issueRepositoryProvider.upsert(issueModel)
                if (!page.defined) {
                    list.add(TimelineModel(issueModel))
                }
                val timeline = it.timeline
                val pageInfo = PageInfoModel(timeline.pageInfo.startCursor, timeline.pageInfo.endCursor,
                    timeline.pageInfo.isHasNextPage, timeline.pageInfo.isHasPreviousPage)
                timeline.nodes?.forEach { node ->
                    when (node) {
                        is AsCommit -> {
                        }
                        is AsIssueComment -> {
                            list.add(TimelineModel(comment = CommentModel(node.id,
                                ShortUserModel(node.author?.login, node.author?.login, avatarUrl = node.author?.avatarUrl.toString()),
                                node.bodyHTML.toString(), node.body, CommentAuthorAssociation.fromName(node.authorAssociation.rawValue()),
                                node.viewerCannotUpdateReasons.map { reason -> CommentCannotUpdateReason.fromName(reason.rawValue()) }.toList(),
                                node.reactionGroups?.map { reaction ->
                                    ReactionGroupModel(reaction.content.rawValue(), reaction.createdAt, CountModel(0),
                                        reaction.isViewerHasReacted)
                                }, node.createdAt, node.updatedAt, node.isViewerCanReact, node.isViewerCanDelete,
                                node.isViewerCanUpdate, node.isViewerDidAuthor, node.isViewerCanMinimize
                            )))
                        }
                        is AsCrossReferencedEvent -> {

                        }
                        is AsClosedEvent -> {

                        }
                        is AsReopenedEvent -> {

                        }
                        is AsSubscribedEvent -> {

                        }
                        is AsUnsubscribedEvent -> {

                        }
                        is AsReferencedEvent -> {

                        }
                        is AsAssignedEvent -> {

                        }
                        is AsUnassignedEvent -> {

                        }
                        is AsLabeledEvent -> {

                        }
                        is AsUnlabeledEvent -> {

                        }
                        is AsMilestonedEvent -> {

                        }
                        is AsDemilestonedEvent -> {

                        }
                        is AsRenamedTitleEvent -> {

                        }
                        is AsLockedEvent -> {

                        }
                        is AsUnlockedEvent -> {

                        }
                        is AsTransferredEvent -> {

                        }
                    }
                }
                return@map Pair(pageInfo, list)
            }
    }
}