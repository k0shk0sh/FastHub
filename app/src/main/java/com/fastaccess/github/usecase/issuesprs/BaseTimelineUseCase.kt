package com.fastaccess.github.usecase.issuesprs

import com.fastaccess.data.model.*
import com.fastaccess.data.model.parcelable.LabelModel
import com.fastaccess.domain.usecase.base.BaseObservableUseCase
import com.fastaccess.extension.*
import github.fragment.*

abstract class BaseTimelineUseCase : BaseObservableUseCase() {


    protected fun getTransferred(node: Transferred): TimelineModel = TimelineModel(
        transferredEventModel = TransferredEventModel(
            node.createdAt, node.actor?.fragments?.shortActor?.toUser(), node.fromRepository?.nameWithOwner
        )
    )

    protected fun getRenamed(node: Renamed): TimelineModel = TimelineModel(
        renamedEventModel = RenamedEventModel(
            node.createdAt, node.actor?.fragments?.shortActor?.toUser(), node.currentTitle, node.previousTitle
        )
    )

    protected fun getDemilestoned(node: Demilestoned): TimelineModel = TimelineModel(
        milestoneEventModel = MilestoneDemilestonedEventModel(
            node.createdAt, node.actor?.fragments?.shortActor?.toUser(), node.milestoneTitle, false
        )
    )

    protected fun getMilestone(node: Milestoned): TimelineModel = TimelineModel(
        milestoneEventModel = MilestoneDemilestonedEventModel(
            node.createdAt, node.actor?.fragments?.shortActor?.toUser(), node.milestoneTitle, true
        )
    )

    protected fun getUnassigned(
        node: UnAssigned,
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
            return TimelineModel(
                assignedEventModel = AssignedUnAssignedEventModel(
                    node.createdAt, node.actor?.fragments?.shortActor?.toUser(), false,
                    arrayListOf(ShortUserModel(node.user?.login, node.user?.login, avatarUrl = node.user?.avatarUrl?.toString()))
                )
            )
        }
        return null
    }

    protected fun getAssigned(
        node: Assigned,
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
            return TimelineModel(
                assignedEventModel = AssignedUnAssignedEventModel(
                    node.createdAt, node.actor?.fragments?.shortActor?.toUser(), true,
                    arrayListOf(ShortUserModel(node.user?.login, node.user?.login, avatarUrl = node.user?.avatarUrl?.toString()))
                )
            )
        }
        return null
    }

    protected fun getUnsubscribed(
        node: Unsubscribed
    ): TimelineModel = TimelineModel(
        subscribedUnsubscribedEvent = SubscribedUnsubscribedEventModel(
            node.createdAt, node.actor?.fragments?.shortActor?.toUser(), false
        )
    )

    protected fun getSubscribed(node: Subscribed): TimelineModel = TimelineModel(
        subscribedUnsubscribedEvent = SubscribedUnsubscribedEventModel(
            node.createdAt, node.actor?.fragments?.shortActor?.toUser(), false
        )
    )

    protected fun getUnlabeled(
        node: UnLabeled,
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
            return TimelineModel(
                labelUnlabeledEvent = LabelUnlabeledEventModel(
                    node.createdAt, node.actor?.fragments?.shortActor?.toUser(), false, arrayListOf(constructLabel(node.label))
                )
            )
        }
        return null
    }

    protected fun getLabel(
        node: Labeled,
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
            return TimelineModel(
                labelUnlabeledEvent = LabelUnlabeledEventModel(
                    node.createdAt, node.actor?.fragments?.shortActor?.toUser(), true, arrayListOf(constructLabel(node.label))
                )
            )
        }
        return null
    }

    protected fun constructLabel(m: Any): LabelModel {
        return when (m) {
            is Labels -> LabelModel(m.name, m.color)
            is Labeled -> LabelModel(m.label.name, m.label.color)
            is Labeled.Label -> LabelModel(m.name, m.color)
            is UnLabeled.Label -> LabelModel(m.fragments.labels.name, m.fragments.labels.color)
            else -> throw IllegalArgumentException("$m is not instance of any Label")
        }
    }


    protected fun getUnlocked(node: Unlocked): TimelineModel = TimelineModel(
        lockUnlockEventModel = LockUnlockEventModel(
            node.createdAt, node.actor?.fragments?.shortActor?.toUser(), null, node.lockable.activeLockReason?.rawValue()
        )
    )

    protected fun getLock(node: Locked): TimelineModel = TimelineModel(
        lockUnlockEventModel = LockUnlockEventModel(
            node.createdAt, node.actor?.fragments?.shortActor?.toUser(), node.lockReason?.rawValue(), node.lockable.activeLockReason?.rawValue(), true
        )
    )

    protected fun getReopened(node: Reopened): TimelineModel {
        return TimelineModel(
            closeOpenEventModel = CloseOpenEventModel(
                node.createdAt, node.actor?.fragments?.shortActor?.toUser()
            )
        )
    }

    protected fun getClosed(node: Closed): TimelineModel {
        val commit = node.closer?.fragments?.commitFragment?.toCommit()
        val pr = node.closer?.fragments?.shortPullRequestRowItem?.toPullRequest()
        return TimelineModel(
            closeOpenEventModel = CloseOpenEventModel(
                node.createdAt, node.actor?.fragments?.shortActor?.toUser(), commit, pr, true
            )
        )
    }

    protected fun getReference(node: Referenced): TimelineModel {
        val issueModel = node.subject.fragments.shortIssueRowItem?.toIssue()
        val pullRequest = node.subject.fragments.shortPullRequestRowItem?.toPullRequest()
        return TimelineModel(
            referencedEventModel = ReferencedEventModel(
                node.commitRepository.nameWithOwner, node.createdAt, ShortUserModel(
                    node.actor?.fragments?.shortActor?.login, node.actor?.fragments?.shortActor?.login,
                    node.actor?.fragments?.shortActor?.url?.toString(),
                    avatarUrl = node.actor?.fragments?.shortActor?.avatarUrl?.toString()
                ), node.isCrossRepository, node.isDirectReference,
                node.commit?.fragments?.commitFragment?.toCommit(), issueModel, pullRequest
            )
        )
    }

    protected fun getCrossReference(node: CrossReferenced): TimelineModel {
        val actor = node.actor?.fragments?.shortActor?.toUser()
        val issueModel = node.source.fragments.shortIssueRowItem?.toIssue()
        val pullRequest = node.source.fragments.shortPullRequestRowItem?.toPullRequest()
        return TimelineModel(
            crossReferencedEventModel = CrossReferencedEventModel(
                node.createdAt, node.referencedAt,
                node.isCrossRepository, node.isWillCloseTarget, actor, issueModel, pullRequest
            )
        )
    }

    protected fun getComment(node: Comment) = TimelineModel(
        comment = CommentModel(
            node.id, node.databaseId,
            ShortUserModel(node.author?.login, node.author?.login, node.author?.url?.toString(), avatarUrl = node.author?.avatarUrl.toString()),
            node.bodyHTML.toString(), node.body, CommentAuthorAssociation.fromName(node.authorAssociation.rawValue()),
            node.viewerCannotUpdateReasons.map { reason -> CommentCannotUpdateReason.fromName(reason.rawValue()) }.toList(),
            node.reactionGroups?.map { it.fragments.reactions.toReactionGroup() }, node.createdAt, node.updatedAt,
            node.isViewerCanReact, node.isViewerCanDelete, node.isViewerCanUpdate, node.isViewerDidAuthor, node.isViewerCanMinimize
        )
    )

}