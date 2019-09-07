package com.fastaccess.github.usecase.issuesprs

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.*
import com.fastaccess.data.persistence.models.MyIssuesPullsModel
import com.fastaccess.data.repository.SchedulerProvider
import com.fastaccess.extension.toReactionGroup
import com.fastaccess.extension.toUser
import com.fastaccess.github.extensions.addIfNotNull
import github.GetPullRequestTimelineQuery
import github.GetPullRequestTimelineQuery.*
import github.type.PullRequestState
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Kosh on 27.01.19.
 */
class GetPullRequestTimelineUseCase @Inject constructor(
    private val apolloClient: ApolloClient,
    private val schedulerProvider: SchedulerProvider
) : BaseTimelineUseCase() {

    var login: String? = null
    var repo: String? = null
    var number: Int? = null
    var page: Input<String> = Input.absent<String>()

    override fun buildObservable(): Observable<Pair<PageInfoModel, ArrayList<TimelineModel>>> {
        val login = login
        val repo = repo
        val number = number

        if (login.isNullOrEmpty() || repo.isNullOrEmpty() || number == null) {
            return Observable.error(Throwable("this should never happen ;)"))
        }

        val observable = Rx2Apollo.from(apolloClient.query(GetPullRequestTimelineQuery(login, repo, number, page)))
            .map { it.data()?.repositoryOwner?.repository?.pullRequest }
            .map { pullRequest ->
                val list = arrayListOf<TimelineModel>()
                val timeline = pullRequest.timelineItems
                val pageInfo = PageInfoModel(
                    timeline.pageInfo.startCursor, timeline.pageInfo.endCursor,
                    timeline.pageInfo.isHasNextPage, timeline.pageInfo.isHasPreviousPage
                )
                timeline.nodes?.forEach { node ->
                    when (node) {
                        is AsIssueComment -> node.fragments.comment?.let { list.add(getComment(it)) }
                        is AsCrossReferencedEvent -> node.fragments.crossReferenced?.let { list.add(getCrossReference(it)) }
                        is AsClosedEvent -> node.fragments.closed?.let {
                            list.add(
                                if (PullRequestState.MERGED == pullRequest.state) {
                                    getClosed(it).apply {
                                        closeOpenEventModel?.pullRequest = MyIssuesPullsModel(state = pullRequest.state.rawValue(), isPr = true)
                                    }
                                } else {
                                    getClosed(it)
                                }
                            )
                        }
                        is AsReopenedEvent -> node.fragments.reopened?.let { list.add(getReopened(it)) }
                        is AsSubscribedEvent -> node.fragments.subscribed?.let { list.add(getSubscribed(it)) }
                        is AsUnsubscribedEvent -> node.fragments.unsubscribed?.let { list.add(getUnsubscribed(it)) }
                        is AsReferencedEvent -> node.fragments.referenced?.let { list.add(getReference(it)) }
                        is AsAssignedEvent -> node.fragments.assigned?.let { list.addIfNotNull(getAssigned(it, list)) }
                        is AsUnassignedEvent -> node.fragments.unAssigned?.let { list.addIfNotNull(getUnassigned(it, list)) }
                        is AsLabeledEvent -> node.fragments.labeled?.let { list.addIfNotNull(getLabel(it, list)) }
                        is AsUnlabeledEvent -> node.fragments.unLabeled?.let { list.addIfNotNull(getUnlabeled(it, list)) }
                        is AsMilestonedEvent -> node.fragments.milestoned?.let { list.add(getMilestone(it)) }
                        is AsDemilestonedEvent -> node.fragments.demilestoned?.let { list.add(getDemilestoned(it)) }
                        is AsRenamedTitleEvent -> node.fragments.renamed?.let { list.add(getRenamed(it)) }
                        is AsLockedEvent -> node.fragments.locked?.let { list.add(getLock(it)) }
                        is AsUnlockedEvent -> node.fragments.unlocked?.let { list.add(getUnlocked(it)) }
                        is AsTransferredEvent -> node.fragments.transferred?.let { list.add(getTransferred(it)) }
                        is AsBaseRefChangedEvent -> list.add(getBaseRefChanged(node))
                        is AsBaseRefForcePushedEvent -> list.add(getBaseRefForcePush(node))
                        is AsHeadRefForcePushedEvent -> list.add(getHeadRefForcePush(node))
                        is AsHeadRefRestoredEvent -> list.add(getHeadRestored(node))
                        is AsHeadRefDeletedEvent -> list.add(getHeadRefDeleted(node))
                        is AsReviewRequestedEvent -> list.add(getRequestForReview(node))
                        is AsReviewDismissedEvent -> list.add(getDismissedReview(node))
                        is AsReviewRequestRemovedEvent -> list.add(getReviewRemoved(node))
                        is AsPullRequestReview -> list.add(getPullRequestReview(node))
                        is AsPullRequestCommitCommentThread -> list.add(getCommitThread(node))
                    }
                }
                return@map Pair(pageInfo, list)
            }

        return observable.subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
    }

    private fun getCommitThread(node: AsPullRequestCommitCommentThread) = TimelineModel(
        commitThread = CommitThreadModel(
            node.path, node.position,
            node.tComment.nodes?.map {
                CommentModel(
                    it.id,
                    it.databaseId,
                    ShortUserModel(it.author?.login, it.author?.login, it.author?.url?.toString(), avatarUrl = it.author?.avatarUrl?.toString()),
                    it.body, CommentAuthorAssociation.fromName(it.authorAssociation.rawValue()),
                    it.reactionGroups?.map { it.fragments.reactions.toReactionGroup() },
                    it.updatedAt, it.updatedAt, it.isViewerCanReact, it.isViewerCanDelete,
                    it.isViewerCanUpdate, it.isViewerDidAuthor, false,
                    it.path, it.position
                )
            }?.firstOrNull()
        )
    )

    private fun getPullRequestReview(node: AsPullRequestReview) = TimelineModel(
        review = ReviewModel(
            node.id,
            node.databaseId,
            ShortUserModel(node.author?.login, node.author?.login, node.author?.url?.toString(), avatarUrl = node.author?.avatarUrl?.toString()),
            node.body,
            node.authorAssociation.rawValue(),
            node.state.rawValue(),
            node.createdAt,
            node.comments.nodes?.map {
                CommentModel(
                    it.id,
                    it.databaseId,
                    ShortUserModel(it.author?.login, it.author?.login, it.author?.url?.toString(), avatarUrl = it.author?.avatarUrl?.toString()),
                    it.body, CommentAuthorAssociation.fromName(it.authorAssociation.rawValue()),
                    it.reactionGroups?.map { it.fragments.reactions.toReactionGroup() },
                    it.updatedAt, it.updatedAt, it.isViewerCanReact, it.isViewerCanDelete,
                    it.isViewerCanUpdate, it.isViewerDidAuthor, false,
                    it.path, it.originalPosition, it.isOutdated, it.diffHunk
                )
            }?.firstOrNull(),
            node.isViewerCanReact,
            node.isViewerCanDelete,
            node.isViewerCanUpdate,
            node.isViewerDidAuthor,
            false,
            node.reactionGroups?.map { it.fragments.reactions.toReactionGroup() }
        )
    )

//    private fun getPullRequestCommit(node: AsPullRequestCommit) = TimelineModel(
//        pullRequestCommit = PullRequestCommitModel(
//            node.id,
//            node.url.toString(),
//            CommitModel(
//                node.prCommit.oid.toString(),
//                ShortUserModel(
//                    node.prCommit.author?.name,
//                    node.prCommit.author?.name,
//                    node.prCommit.author?.user?.url.toString(),
//                    avatarUrl = node.prCommit.author?.avatarUrl?.toString() ?: node.prCommit.author?.user?.avatarUrl?.toString()
//                ),
//                node.prCommit.message,
//                node.prCommit.abbreviatedOid,
//                node.prCommit.commitUrl.toString(),
//                node.prCommit.authoredDate,
//                node.prCommit.isCommittedViaWeb,
//                node.prCommit.history.nodes?.lastOrNull()?.status?.state?.rawValue()
//            )
//        )
//    )

    private fun getReviewRemoved(node: AsReviewRequestRemovedEvent) = TimelineModel(
        reviewRequestRemoved = ReviewRequestRemovedModel(
            node.actor?.fragments?.shortActor?.toUser(),
            when (val m = node.requestedReviewer) {
                is AsUser1 -> ShortUserModel(m.login, m.login, m.url.toString(), avatarUrl = m.userAvatar.toString())
                is AsTeam1 -> ShortUserModel(m.name, m.name, m.url.toString(), avatarUrl = m.teamAvatar.toString())
                is AsMannequin1 -> ShortUserModel(m.login, m.login, m.url.toString(), avatarUrl = m.monnequinAvatar.toString())
                else -> null
            },
            node.createdAt,
            node.requestedReviewer is AsUser1,
            node.requestedReviewer is AsTeam1,
            node.requestedReviewer is AsMannequin1
        )
    )

    private fun getDismissedReview(node: AsReviewDismissedEvent) = TimelineModel(
        reviewDismissed = ReviewDismissedModel(
            node.actor?.fragments?.shortActor?.toUser(),
            node.createdAt,
            node.dismissalMessage,
            node.previousReviewState.rawValue(),
            node.url.toString()
        )
    )

    private fun getRequestForReview(node: AsReviewRequestedEvent) = TimelineModel(
        reviewRequested = ReviewRequestedModel(
            node.actor?.fragments?.shortActor?.toUser(),
            when (val m = node.requestedReviewer) {
                is AsUser -> ShortUserModel(m.login, m.login, m.url.toString(), avatarUrl = m.userAvatar.toString())
                is AsTeam -> ShortUserModel(m.name, m.name, m.url.toString(), avatarUrl = m.teamAvatar.toString())
                is AsMannequin -> ShortUserModel(m.login, m.login, m.url.toString(), avatarUrl = m.monnequinAvatar.toString())
                else -> null
            },
            node.createdAt,
            node.requestedReviewer is AsUser,
            node.requestedReviewer is AsTeam,
            node.requestedReviewer is AsMannequin
        )
    )

    private fun getHeadRefDeleted(node: AsHeadRefDeletedEvent) = TimelineModel(
        headRefDeleted = HeadRefDeletedModel(
            node.actor?.fragments?.shortActor?.toUser(),
            node.headRefName,
            node.createdAt
        )
    )

    private fun getHeadRestored(node: AsHeadRefRestoredEvent) = TimelineModel(
        headRefRestored = HeadRefRestoredModel(
            node.actor?.fragments?.shortActor?.toUser(),
            node.createdAt
        )
    )

    private fun getHeadRefForcePush(node: AsHeadRefForcePushedEvent): TimelineModel = TimelineModel(
        baseRefForcePush = BaseRefForcePushModel(
            node.actor?.fragments?.shortActor?.toUser(),
            node.beforeCommit?.abbreviatedOid,
            node.afterCommit?.abbreviatedOid,
            node.createdAt,
            false
        )
    )

    private fun getBaseRefForcePush(node: AsBaseRefForcePushedEvent) = TimelineModel(
        baseRefForcePush = BaseRefForcePushModel(
            node.actor?.fragments?.shortActor?.toUser(),
            node.beforeCommit?.abbreviatedOid,
            node.afterCommit?.abbreviatedOid,
            node.createdAt
        )
    )

    private fun getBaseRefChanged(node: AsBaseRefChangedEvent) = TimelineModel(
        baseRefChangedEvent = BaseRefChangedModel(
            node.databaseId,
            node.actor?.fragments?.shortActor?.toUser(),
            node.createdAt
        )
    )
}