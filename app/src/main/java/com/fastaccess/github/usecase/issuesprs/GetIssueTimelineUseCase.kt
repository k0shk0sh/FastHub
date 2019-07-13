package com.fastaccess.github.usecase.issuesprs

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.rx2.Rx2Apollo
import com.fastaccess.data.model.*
import com.fastaccess.data.model.parcelable.LabelModel
import com.fastaccess.data.repository.IssueRepositoryProvider
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.extension.*
import com.fastaccess.github.extensions.addIfNotNull
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
    var page: Input<String> = Input.absent<String>()

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
                        is AsSubscribedEvent -> list.add(getSubscribed(node))
                        is AsUnsubscribedEvent -> list.add(getUnsubscribed(node))
                        is AsReferencedEvent -> list.add(getReference(node))
                        is AsAssignedEvent -> list.addIfNotNull(getAssigned(node, list))
                        is AsUnassignedEvent -> list.addIfNotNull(getUnassigned(node, list))
                        is AsLabeledEvent -> list.addIfNotNull(getLabel(node, list))
                        is AsUnlabeledEvent -> list.addIfNotNull(getUnlabeled(node, list))
                        is AsMilestonedEvent -> list.add(getMilestone(node))
                        is AsDemilestonedEvent -> list.add(getDemilestoned(node))
                        is AsRenamedTitleEvent -> list.add(getRenamed(node))
                        is AsLockedEvent -> list.add(getLock(node))
                        is AsUnlockedEvent -> list.add(getUnlocked(node))
                        is AsTransferredEvent -> list.add(getTransferred(node))
                    }
                }
                return@map Pair(pageInfo, list)
            }
    }

    private fun getTransferred(node: AsTransferredEvent): TimelineModel = TimelineModel(transferredEventModel = TransferredEventModel(
        node.createdAt, node.actor?.fragments?.shortActor?.toUser(), node.fromRepository?.nameWithOwner
    ))

    private fun getRenamed(node: AsRenamedTitleEvent): TimelineModel = TimelineModel(renamedEventModel = RenamedEventModel(
        node.createdAt, node.actor?.fragments?.shortActor?.toUser(), node.currentTitle, node.previousTitle
    ))

    private fun getDemilestoned(node: AsDemilestonedEvent): TimelineModel = TimelineModel(milestoneEventModel = MilestoneDemilestonedEventModel(
        node.createdAt, node.actor?.fragments?.shortActor?.toUser(), node.milestoneTitle, false
    ))

    private fun getMilestone(node: AsMilestonedEvent): TimelineModel = TimelineModel(milestoneEventModel = MilestoneDemilestonedEventModel(
        node.createdAt, node.actor?.fragments?.shortActor?.toUser(), node.milestoneTitle, true
    ))

    private fun getUnassigned(
        node: AsUnassignedEvent,
        list: ArrayList<TimelineModel>
    ): TimelineModel? {
        var shouldAdd = true
        list.filter { it.assignedEventModel != null }.map {
            if (it.assignedEventModel?.createdAt?.time == node.createdAt.time) {
                it.assignedEventModel?.users?.add(ShortUserModel(node.user?.login, node.user?.login, avatarUrl = node.user?.avatarUrl?.toString()))
                shouldAdd = false
            }
        }
        if (shouldAdd) {
            return TimelineModel(assignedEventModel = AssignedUnAssignedEventModel(
                node.createdAt, node.actor?.fragments?.shortActor?.toUser(), false,
                arrayListOf(ShortUserModel(node.user?.login, node.user?.login, avatarUrl = node.user?.avatarUrl?.toString()))
            ))
        }
        return null
    }

    private fun getAssigned(
        node: AsAssignedEvent,
        list: ArrayList<TimelineModel>
    ): TimelineModel? {
        var shouldAdd = true
        list.filter { it.assignedEventModel != null }.map {
            if (it.assignedEventModel?.createdAt?.time == node.createdAt.time) {
                it.assignedEventModel?.users?.add(ShortUserModel(node.user?.login, node.user?.login, avatarUrl = node.user?.avatarUrl?.toString()))
                shouldAdd = false
            }
        }
        if (shouldAdd) {
            return TimelineModel(assignedEventModel = AssignedUnAssignedEventModel(
                node.createdAt, node.actor?.fragments?.shortActor?.toUser(), true,
                arrayListOf(ShortUserModel(node.user?.login, node.user?.login, avatarUrl = node.user?.avatarUrl?.toString()))
            ))
        }
        return null
    }

    private fun getUnsubscribed(
        node: AsUnsubscribedEvent
    ): TimelineModel = TimelineModel(subscribedUnsubscribedEvent = SubscribedUnsubscribedEventModel(
        node.createdAt, node.actor?.fragments?.shortActor?.toUser(), false
    ))

    private fun getSubscribed(node: AsSubscribedEvent): TimelineModel = TimelineModel(subscribedUnsubscribedEvent = SubscribedUnsubscribedEventModel(
        node.createdAt, node.actor?.fragments?.shortActor?.toUser(), false
    ))

    private fun getUnlabeled(
        node: AsUnlabeledEvent,
        list: ArrayList<TimelineModel>
    ): TimelineModel? {
        var shouldAdd = true
        list.filter { it.labelUnlabeledEvent != null }.map {
            if (it.labelUnlabeledEvent?.createdAt?.time == node.createdAt.time) {
                it.labelUnlabeledEvent?.labels?.add(constructLabel(node.label))
                shouldAdd = false
            }
        }
        if (shouldAdd) {
            return TimelineModel(labelUnlabeledEvent = LabelUnlabeledEventModel(
                node.createdAt, node.actor?.fragments?.shortActor?.toUser(), false, arrayListOf(constructLabel(node.label))
            ))
        }
        return null
    }

    private fun getLabel(
        node: AsLabeledEvent,
        list: ArrayList<TimelineModel>
    ): TimelineModel? {
        var shouldAdd = true
        list.filter { it.labelUnlabeledEvent != null }.map {
            if (it.labelUnlabeledEvent?.createdAt?.time == node.createdAt.time) {
                it.labelUnlabeledEvent?.labels?.add(constructLabel(node.label))
                shouldAdd = false
            }
        }
        if (shouldAdd) {
            return TimelineModel(labelUnlabeledEvent = LabelUnlabeledEventModel(
                node.createdAt, node.actor?.fragments?.shortActor?.toUser(), true, arrayListOf(constructLabel(node.label))
            ))
        }
        return null
    }

    private fun constructLabel(m: Any): LabelModel {
        return when (m) {
            is Label -> LabelModel(m.name, m.color)
            is Label1 -> LabelModel(m.name, m.color)
            else -> throw IllegalArgumentException("$m is not instance of any Label")
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
        node.reactionGroups?.map { it.fragments.reactions.toReactionGroup() }, node.createdAt, node.updatedAt,
        node.isViewerCanReact, node.isViewerCanDelete, node.isViewerCanUpdate, node.isViewerDidAuthor, node.isViewerCanMinimize
    ))

    private fun getCommit(node: AsCommit) = TimelineModel(commit = node.fragments.commitFragment?.toCommit())
}