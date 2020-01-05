package com.fastaccess.data.model

import com.fastaccess.data.model.parcelable.LabelModel
import com.fastaccess.data.persistence.models.IssueModel
import com.fastaccess.data.persistence.models.MyIssuesPullsModel
import com.fastaccess.data.persistence.models.PullRequestModel
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by Kosh on 31.01.19.
 */
data class TimelineModel(
    @SerializedName("issue") val issue: IssueModel? = null,
    @SerializedName("commit") val commit: CommitModel? = null,
    @SerializedName("comment") val comment: CommentModel? = null,
    @SerializedName("crossReferencedEvent") val crossReferencedEventModel: CrossReferencedEventModel? = null,
    @SerializedName("closeOpenEvent") val closeOpenEventModel: CloseOpenEventModel? = null,
    @SerializedName("referenceEvent") val referencedEventModel: ReferencedEventModel? = null,
    @SerializedName("lockUnlockEvent") val lockUnlockEventModel: LockUnlockEventModel? = null,
    @SerializedName("labelUnlabeledEvent") val labelUnlabeledEvent: LabelUnlabeledEventModel? = null,
    @SerializedName("subscribeUnsubscribedEvent") val subscribedUnsubscribedEvent: SubscribedUnsubscribedEventModel? = null,
    @SerializedName("assignedUnassignedEvent") val assignedEventModel: AssignedUnAssignedEventModel? = null,
    @SerializedName("milestoneDemilestoneEvent") val milestoneEventModel: MilestoneDemilestonedEventModel? = null,
    @SerializedName("renamedEvent") val renamedEventModel: RenamedEventModel? = null,
    @SerializedName("transferredEvent") val transferredEventModel: TransferredEventModel? = null,
    @SerializedName("pullRequest") val pullRequest: PullRequestModel? = null,
    @SerializedName("baseRefChanged") val baseRefChangedEvent: BaseRefChangedModel? = null,
    @SerializedName("baseRefForcePush") val baseRefForcePush: BaseRefForcePushModel? = null,
    @SerializedName("headRefRestored") var headRefRestored: HeadRefRestoredModel? = null,
    @SerializedName("headRefDeleted") var headRefDeleted: HeadRefDeletedModel? = null,
    @SerializedName("reviewRequested") var reviewRequested: ReviewRequestedModel? = null,
    @SerializedName("reviewDismissed") var reviewDismissed: ReviewDismissedModel? = null,
    @SerializedName("reviewRequestRemoved") var reviewRequestRemoved: ReviewRequestRemovedModel? = null,
    @SerializedName("pullRequestCommit") var pullRequestCommit: PullRequestCommitModel? = null,
    @SerializedName("review") var review: ReviewModel? = null,
    @SerializedName("commitThread") var commitThread: CommitThreadModel? = null,
    @SerializedName("dividerId") var dividerId: String? = null
)

data class CommitModel(
    @SerializedName("id") var id: String? = null,
    @SerializedName("author") var author: ShortUserModel? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("abbreviatedOid") var abbreviatedOid: String? = null,
    @SerializedName("commitUrl") var commitUrl: String? = null,
    @SerializedName("authoredDate") var authoredDate: Date? = null,
    @SerializedName("committedViaWeb") var committedViaWeb: Boolean? = null,
    @SerializedName("state") var state: String? = null
)

data class CommentModel(
    @SerializedName("id") var id: String? = null,
    @SerializedName("databaseId") var databaseId: Int? = null,
    @SerializedName("author") var author: ShortUserModel? = null,
    @SerializedName("body") var body: String? = null,
    @SerializedName("authorAssociation") var authorAssociation: CommentAuthorAssociation? = null,
    @SerializedName("reactionGroups") var reactionGroups: List<ReactionGroupModel>? = null,
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("updatedAt") var updatedAt: Date? = null,
    @SerializedName("viewerCanReact") var viewerCanReact: Boolean? = null,
    @SerializedName("viewerCanDelete") var viewerCanDelete: Boolean? = null,
    @SerializedName("viewerCanUpdate") var viewerCanUpdate: Boolean? = null,
    @SerializedName("viewerDidAuthor") var viewerDidAuthor: Boolean? = null,
    @SerializedName("viewerCanMinimize") var viewerCanMinimize: Boolean? = null,
    @SerializedName("path") var path: String? = null,
    @SerializedName("originalPosition") var originalPosition: Int? = null,
    @SerializedName("outdated") var outdated: Boolean? = null,
    @SerializedName("diffHunk") var diffHunk: String? = null
)

data class CrossReferencedEventModel(
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("referencedAt") var referencedAt: Date? = null,
    @SerializedName("isCrossRepository") var isCrossRepository: Boolean? = null,
    @SerializedName("willCloseTarget") var willCloseTarget: Boolean? = null,
    @SerializedName("actor") var actor: ShortUserModel? = null,
    @SerializedName("issue") var issue: MyIssuesPullsModel? = null,
    @SerializedName("pullRequest") var pullRequest: MyIssuesPullsModel? = null
)

data class ReferencedEventModel(
    @SerializedName("repoName") var repoName: String? = null,
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("actor") var actor: ShortUserModel? = null,
    @SerializedName("isCrossRepository") var isCrossRepository: Boolean? = null,
    @SerializedName("isDirectReference") var isDirectReference: Boolean? = null,
    @SerializedName("commit") var commit: CommitModel? = null,
    @SerializedName("issue") var issue: MyIssuesPullsModel? = null,
    @SerializedName("pullRequest") var pullRequestRowItem: MyIssuesPullsModel? = null
)

data class CloseOpenEventModel(
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("actor") var actor: ShortUserModel? = null,
    @SerializedName("commit") var commit: CommitModel? = null,
    @SerializedName("pullRequest") var pullRequest: MyIssuesPullsModel? = null,
    @SerializedName("isClosed") var isClosed: Boolean? = null
)

data class LockUnlockEventModel(
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("actor") var actor: ShortUserModel? = null,
    @SerializedName("lockReason") var lockReason: String? = null,
    @SerializedName("lockable") var lockable: String? = null,
    @SerializedName("isLock") var isLock: Boolean? = null
)

data class LabelUnlabeledEventModel(
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("actor") var actor: ShortUserModel? = null,
    @SerializedName("isLock") var isLabel: Boolean? = null,
    @SerializedName("labels") var labels: ArrayList<LabelModel> = arrayListOf()
)

data class SubscribedUnsubscribedEventModel(
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("actor") var actor: ShortUserModel? = null,
    @SerializedName("isSubscribe") var isSubscribe: Boolean? = null
)

data class AssignedUnAssignedEventModel(
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("actor") var actor: ShortUserModel? = null,
    @SerializedName("isAssigned") var isAssigned: Boolean? = null,
    @SerializedName("user") var users: ArrayList<ShortUserModel> = arrayListOf()
)

data class MilestoneDemilestonedEventModel(
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("actor") var actor: ShortUserModel? = null,
    @SerializedName("milestoneTitle") var milestoneTitle: String? = null,
    @SerializedName("isMilestone") var isMilestone: Boolean? = null
)

data class RenamedEventModel(
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("actor") var actor: ShortUserModel? = null,
    @SerializedName("currentTitle") var currentTitle: String? = null,
    @SerializedName("previousTitle") var previousTitle: String? = null
)

data class TransferredEventModel(
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("actor") var actor: ShortUserModel? = null,
    @SerializedName("fromRepository") var fromRepository: String? = null
)

data class BaseRefChangedModel(
    @SerializedName("databaseId") var databaseId: Int? = null,
    @SerializedName("actor") var actor: ShortUserModel? = null,
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("isBase") var isBase: Boolean = true
)

data class BaseRefForcePushModel(
    @SerializedName("actor") var actor: ShortUserModel? = null,
    @SerializedName("beforeCommit") var beforeCommit: String? = null,
    @SerializedName("afterCommit") var afterCommit: String? = null,
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("isBase") var isBase: Boolean = true
)

data class HeadRefRestoredModel(
    @SerializedName("actor") var actor: ShortUserModel? = null,
    @SerializedName("createdAt") var createdAt: Date? = null
)

data class HeadRefDeletedModel(
    @SerializedName("actor") var actor: ShortUserModel? = null,
    @SerializedName("headRefName") var headRefName: String? = null,
    @SerializedName("createdAt") var createdAt: Date? = null
)

data class ReviewRequestedModel(
    @SerializedName("actor") var actor: ShortUserModel? = null,
    @SerializedName("reviewer") var reviewer: ShortUserModel? = null,
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("isUser") var isUser: Boolean = true,
    @SerializedName("isTeam") var isTeam: Boolean = false,
    @SerializedName("isMannequin") var isMannequin: Boolean = false
)

data class ReviewDismissedModel(
    @SerializedName("actor") var actor: ShortUserModel? = null,
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("dismissalMessage") var dismissalMessage: String? = null,
    @SerializedName("previousReviewState") var previousReviewState: String? = null,
    @SerializedName("url") var url: String? = null
)

data class ReviewRequestRemovedModel(
    @SerializedName("actor") var actor: ShortUserModel? = null,
    @SerializedName("reviewer") var reviewer: ShortUserModel? = null,
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("isUser") var isUser: Boolean = true,
    @SerializedName("isTeam") var isTeam: Boolean = false,
    @SerializedName("isMannequin") var isMannequin: Boolean = false
)

data class PullRequestCommitModel(
    @SerializedName("id") var id: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("commit") var commit: CommitModel? = null
)

data class ReviewModel(
    @SerializedName("id") var id: String? = null,
    @SerializedName("databaseId") var databaseId: Int? = null,
    @SerializedName("author") var author: ShortUserModel? = null,
    @SerializedName("body") var body: String? = null,
    @SerializedName("authorAssociation") var authorAssociation: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("comment") var comment: CommentModel? = null,
    @SerializedName("viewerCanReact") var viewerCanReact: Boolean? = null,
    @SerializedName("viewerCanDelete") var viewerCanDelete: Boolean? = null,
    @SerializedName("viewerCanUpdate") var viewerCanUpdate: Boolean? = null,
    @SerializedName("viewerDidAuthor") var viewerDidAuthor: Boolean? = null,
    @SerializedName("viewerCanMinimize") var viewerCanMinimize: Boolean? = null,
    @SerializedName("reactionGroups") var reactionGroups: List<ReactionGroupModel>? = null,
    @SerializedName("isReviewBody") var isReviewBody: Boolean? = null
)

data class CommitThreadModel(
    @SerializedName("path") var path: String? = null,
    @SerializedName("position") var position: Int? = null,
    @SerializedName("comment") var comment: CommentModel? = null
)

enum class CommentAuthorAssociation(val value: String) {
    MEMBER("MEMBER"),
    OWNER("OWNER"),
    COLLABORATOR("COLLABORATOR"),
    CONTRIBUTOR("CONTRIBUTOR"),
    FIRST_TIME_CONTRIBUTOR("FIRST_TIME_CONTRIBUTOR"),
    FIRST_TIMER("FIRST_TIMER"),
    NONE("NONE");

    companion object {
        fun fromName(name: String): CommentAuthorAssociation? = values().firstOrNull { it.value == name }
    }
}

enum class CommentCannotUpdateReason(val value: kotlin.String) {
    INSUFFICIENT_ACCESS("INSUFFICIENT_ACCESS"),
    LOCKED("LOCKED"),
    LOGIN_REQUIRED("LOGIN_REQUIRED"),
    MAINTENANCE("LOGIN_REQUIRED"),
    VERIFIED_EMAIL_REQUIRED("LOGIN_REQUIRED"),
    DENIED("LOGIN_REQUIRED");

    companion object {
        fun fromName(name: String): CommentCannotUpdateReason? = values().firstOrNull { it.value == name }
    }
}