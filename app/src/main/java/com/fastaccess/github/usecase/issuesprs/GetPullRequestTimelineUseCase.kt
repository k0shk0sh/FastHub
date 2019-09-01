package com.fastaccess.github.usecase.issuesprs

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.*
import com.fastaccess.data.persistence.models.MyIssuesPullsModel
import com.fastaccess.data.repository.SchedulerProvider
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

    override fun buildObservable(): Observable<Pair<PageInfoModel, List<TimelineModel>>> {
        val login = login
        val repo = repo
        val number = number

        if (login.isNullOrEmpty() || repo.isNullOrEmpty() || number == null) {
            return Observable.error(Throwable("this should never happen ;)"))
        }

        return Rx2Apollo.from(apolloClient.query(GetPullRequestTimelineQuery(login, repo, number, page)))
            .subscribeOn(schedulerProvider.ioThread())
            .observeOn(schedulerProvider.uiThread())
            .map { it.data()?.repositoryOwner?.repository?.pullRequest }
            .map { pullRequest ->
                val list = arrayListOf<TimelineModel>()
                val timeline = pullRequest.timelineItems
                val pageInfo = PageInfoModel(
                    timeline.pageInfo.startCursor, timeline.pageInfo.endCursor,
                    timeline.pageInfo.isHasNextPage, timeline.pageInfo.isHasPreviousPage
                )
                /**
                 * [ISSUE_COMMENT, CLOSED_EVENT, REOPENED_EVENT, REFERENCED_EVENT, ASSIGNED_EVENT,
                UNASSIGNED_EVENT, LABELED_EVENT, UNLABELED_EVENT, MILESTONED_EVENT, DEMILESTONED_EVENT, RENAMED_TITLE_EVENT,
                LOCKED_EVENT, UNLOCKED_EVENT, TRANSFERRED_EVENT, PULL_REQUEST_COMMIT, PULL_REQUEST_COMMIT_COMMENT_THREAD,
                PULL_REQUEST_REVIEW, PULL_REQUEST_REVIEW_THREAD, HEAD_REF_DELETED_EVENT, HEAD_REF_FORCE_PUSHED_EVENT,
                MERGED_EVENT, MERGED_EVENT, REVIEW_DISMISSED_EVENT, REVIEW_REQUESTED_EVENT,
                REVIEW_REQUEST_REMOVED_EVENT, READY_FOR_REVIEW_EVENT]
                 */
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
                    }
                }
                return@map Pair(pageInfo, list)
            }
    }

    private fun getDismissedReview(node: AsReviewDismissedEvent): TimelineModel = TimelineModel(
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