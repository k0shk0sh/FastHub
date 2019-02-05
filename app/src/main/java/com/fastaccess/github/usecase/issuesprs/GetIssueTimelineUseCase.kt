package com.fastaccess.github.usecase.issuesprs

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.*
import com.fastaccess.data.persistence.models.IssueModel
import com.fastaccess.data.repository.IssueRepositoryProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.extension.toCommit
import com.fastaccess.extension.toIssue
import com.fastaccess.extension.toPullRequest
import com.fastaccess.extension.toUser
import github.GetIssueTimelineQuery
import github.GetIssueTimelineQuery.*
import io.reactivex.Observable
import org.jetbrains.annotations.Nullable
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
                addIssue(it, list)
                val timeline = it.timeline
                val pageInfo = PageInfoModel(timeline.pageInfo.startCursor, timeline.pageInfo.endCursor,
                    timeline.pageInfo.isHasNextPage, timeline.pageInfo.isHasPreviousPage)
                timeline.nodes?.forEach { node ->
                    when (node) {
                        is AsCommit -> list.add(getCommit(node))
                        is AsIssueComment -> list.add(getComment(node))
                        is AsCrossReferencedEvent -> list.add(getCrossReference(node))
                        is AsClosedEvent -> list.add(getClosed(node))
                        is AsReopenedEvent -> list.add(getReopened(node))
                        is AsSubscribedEvent -> {
                        }
                        is AsUnsubscribedEvent -> {
                        }
                        is AsReferencedEvent -> list.add(getReference(node))
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
                        is AsLockedEvent -> list.add(getLock(node))
                        is AsUnlockedEvent -> list.add(getUnlocked(node))
                        is AsTransferredEvent -> {

                        }
                    }
                }
                return@map Pair(pageInfo, list)
            }
    }

    private fun getUnlocked(node: AsUnlockedEvent): TimelineModel = TimelineModel(lockUnlockEventModel = LockUnlockEventModel(
        node.createdAt, node.actor?.fragments?.shortActor?.toUser(), null, node.lockable.activeLockReason?.rawValue()
    ))

    private fun getLock(node: AsLockedEvent): TimelineModel = TimelineModel(lockUnlockEventModel = LockUnlockEventModel(
        node.createdAt, node.actor?.fragments?.shortActor?.toUser(), node.lockReason?.rawValue(), node.lockable.activeLockReason?.rawValue(), true
    ))

    private fun getReopened(node: AsReopenedEvent): TimelineModel {
        return TimelineModel(closeOpenEventModel = CloseOpenEventModel(
            node.createdAt, node.actor?.fragments?.shortActor?.toUser())
        )
    }

    private fun getClosed(node: AsClosedEvent): TimelineModel {
        val commit = node.closer?.fragments?.commitFragment?.toCommit()
        val pr = node.closer?.fragments?.shortPullRequestRowItem?.toPullRequest()
        return TimelineModel(closeOpenEventModel = CloseOpenEventModel(
            node.createdAt, node.actor?.fragments?.shortActor?.toUser(), commit, pr, true)
        )
    }

    private fun getReference(node: AsReferencedEvent): TimelineModel {
        val issueModel = node.subject.fragments.shortIssueRowItem?.toIssue()
        val pullRequest = node.subject.fragments.shortPullRequestRowItem?.toPullRequest()
        return TimelineModel(referencedEventModel = ReferencedEventModel(
            node.commitRepository.nameWithOwner, node.createdAt, ShortUserModel(
            node.actor?.fragments?.shortActor?.login, node.actor?.fragments?.shortActor?.login, node.actor?.fragments?.shortActor?.url?.toString(),
            avatarUrl = node.actor?.fragments?.shortActor?.avatarUrl?.toString()), node.isCrossRepository, node.isDirectReference,
            node.commit?.fragments?.commitFragment?.toCommit(), issueModel, pullRequest))
    }

    private fun getCrossReference(node: AsCrossReferencedEvent): TimelineModel {
        val actor = node.actor?.fragments?.shortActor?.toUser()
        val issueModel = node.source.fragments.shortIssueRowItem?.toIssue()
        val pullRequest = node.source.fragments.shortPullRequestRowItem?.toPullRequest()
        return TimelineModel(crossReferencedEventModel = CrossReferencedEventModel(node.createdAt, node.referencedAt,
            node.isCrossRepository, node.isWillCloseTarget, actor, issueModel, pullRequest))
    }

    private fun getComment(node: AsIssueComment) = TimelineModel(comment = CommentModel(node.id,
        ShortUserModel(node.author?.login, node.author?.login, avatarUrl = node.author?.avatarUrl.toString()),
        node.bodyHTML.toString(), node.body, CommentAuthorAssociation.fromName(node.authorAssociation.rawValue()),
        node.viewerCannotUpdateReasons.map { reason -> CommentCannotUpdateReason.fromName(reason.rawValue()) }.toList(),
        node.reactionGroups?.map { reaction ->
            ReactionGroupModel(reaction.content.rawValue(), reaction.createdAt, CountModel(0),
                reaction.isViewerHasReacted)
        }, node.createdAt, node.updatedAt, node.isViewerCanReact, node.isViewerCanDelete,
        node.isViewerCanUpdate, node.isViewerDidAuthor, node.isViewerCanMinimize
    ))

    private fun getCommit(node: AsCommit) = TimelineModel(commit = node.fragments?.commitFragment?.toCommit())

    private fun addIssue(
        it: @Nullable Issue,
        list: ArrayList<TimelineModel>
    ) {
        val fullIssue = it.fragments.fullIssue
        val issueModel = IssueModel(fullIssue.id, fullIssue.databaseId, fullIssue.number, fullIssue.activeLockReason?.rawValue(),
            fullIssue.body, fullIssue.bodyHTML.toString(), fullIssue.closedAt, fullIssue.createdAt, fullIssue.updatedAt,
            fullIssue.state.rawValue(), fullIssue.title, fullIssue.viewerSubscription?.rawValue(),
            ShortUserModel(fullIssue.author?.login, fullIssue.author?.login, fullIssue.author?.url?.toString(),
                avatarUrl = fullIssue.author?.avatarUrl?.toString()), EmbeddedRepoModel(fullIssue.repository.nameWithOwner),
            CountModel(fullIssue.userContentEdits?.totalCount), fullIssue.reactionGroups?.map {
            ReactionGroupModel(it.content.rawValue(), it.createdAt, CountModel(it.users.totalCount), it.isViewerHasReacted)
        }, fullIssue.viewerCannotUpdateReasons.map { it.rawValue() }, fullIssue.isClosed, fullIssue.isCreatedViaEmail,
            fullIssue.isLocked, fullIssue.isViewerCanReact, fullIssue.isViewerCanSubscribe, fullIssue.isViewerCanUpdate,
            fullIssue.isViewerDidAuthor)
        issueRepositoryProvider.upsert(issueModel)
        if (!page.defined) {
            list.add(TimelineModel(issueModel))
        }
    }
}