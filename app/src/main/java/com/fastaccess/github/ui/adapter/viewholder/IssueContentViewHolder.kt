package com.fastaccess.github.ui.adapter.viewholder

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.model.*
import com.fastaccess.github.R
import com.fastaccess.github.extensions.route
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.github.base.adapter.BaseViewHolder
import com.fastaccess.markdown.spans.LabelSpan
import com.fastaccess.markdown.widget.SpannableBuilder
import github.type.PullRequestReviewState
import github.type.PullRequestState
import github.type.StatusState
import kotlinx.android.synthetic.main.issue_content_row_item.view.*

/**
 * Created by Kosh on 04.02.19.
 */
class IssueContentViewHolder(parent: ViewGroup) : BaseViewHolder<TimelineModel>(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.issue_content_row_item, parent, false)
) {
    override fun canDivide(): Boolean = false

    override fun bind(item: TimelineModel) {
        itemView.text.text = ""
        itemView.userIcon.setImageDrawable(null)
        itemView.stateIcon.setImageDrawable(null)
        item.commit?.let(this::presentCommit)
        item.crossReferencedEventModel?.let(this::presentCrossReference)
        item.referencedEventModel?.let(this::presentReference)
        item.closeOpenEventModel?.let(this::presentClosedReopen)
        item.lockUnlockEventModel?.let(this::presentLockUnlock)
        item.labelUnlabeledEvent?.let(this::presentLabels)
        item.subscribedUnsubscribedEvent?.let(this::presentSubscribeUnsubscribed)
        item.assignedEventModel?.let(this::presentAssignedAndUnassigned)
        item.milestoneEventModel?.let(this::presentMilestoneDemilestone)
        item.renamedEventModel?.let(this::presentRenamed)
        item.transferredEventModel?.let(this::presentTransferred)
        item.baseRefChangedEvent?.let(this::presentBaseRefChanged)
        item.baseRefForcePush?.let(this::presentForcePush)
        item.headRefRestored?.let(this::presentRefRestored)
        item.headRefDeleted?.let(this::presentHeadDeleted)
        item.reviewRequested?.let(this::presentReviewRequest)
        item.reviewDismissed?.let(this::presenetReviewDismissed)
        item.reviewRequestRemoved?.let(this::presenetReviewRequestRemoved)
        item.pullRequestCommit?.let(this::presentPrCommit)
        item.review?.let(this::presentReview)
    }

    @SuppressLint("DefaultLocale")
    private fun presentReview(model: ReviewModel) {
        itemView.apply {
            val icon = when (model.state) {
                PullRequestReviewState.APPROVED.rawValue() -> R.drawable.ic_done
                PullRequestReviewState.CHANGES_REQUESTED.rawValue() -> R.drawable.ic_clear
                PullRequestReviewState.COMMENTED.rawValue() -> R.drawable.ic_comment
                else -> 0
            }
            if (icon != 0) {
                stateIcon.setImageResource(icon)
                stateIcon.isVisible = true
            } else {
                stateIcon.isVisible = false
            }
            userIcon.loadAvatar(model.author?.avatarUrl, model.author?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.author?.login)
                .space()
                .apply {
                    append(model.state?.replace("_", " ")?.toLowerCase() ?: "")
                    val body = model.body
                    if (!body.isNullOrEmpty()) {
                        newline()
                        append(body)
                        space()
                    } else {
                        space()
                    }
                }
                .append(model.createdAt?.timeAgo())
        }
    }

    private fun presentPrCommit(model: PullRequestCommitModel) {
        itemView.apply {
            val icon = when (model.commit?.state) {
                StatusState.ERROR.rawValue(), StatusState.FAILURE.rawValue() -> R.drawable.ic_state_error
                StatusState.SUCCESS.rawValue() -> R.drawable.ic_state_success
                StatusState.PENDING.rawValue() -> R.drawable.ic_state_pending
                else -> 0
            }
            if (icon == 0) {
                stateIcon.isVisible = false
            } else {
                stateIcon.setImageResource(icon)
                stateIcon.isVisible = true
            }
            userIcon.loadAvatar(model.commit?.author?.avatarUrl, model.commit?.author?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.commit?.author?.login)
                .space()
                .append("committed")
                .space()
                .url("${model.commit?.abbreviatedOid}", View.OnClickListener { view ->
                    model.commit?.commitUrl?.let { view.context.route(it) }
                })
                .space()
                .append("${model.commit?.message} " ?: "")
                .append(model.commit?.authoredDate?.timeAgo())
        }
    }

    @SuppressLint("DefaultLocale")
    private fun presenetReviewRequestRemoved(model: ReviewRequestRemovedModel) {
        itemView.apply {
            stateIcon.setImageResource(R.drawable.ic_track_changes)
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append("dismissed ${model.reviewer?.login} review ")
                .append(model.createdAt?.timeAgo())
        }
    }

    @SuppressLint("DefaultLocale")
    private fun presenetReviewDismissed(model: ReviewDismissedModel) {
        itemView.apply {
            stateIcon.setImageResource(R.drawable.ic_track_changes)
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append("dismissed their review (${model.previousReviewState?.toLowerCase()})")
                .apply {
                    val msg = model.dismissalMessage
                    if (!msg.isNullOrEmpty()) {
                        newline()
                        append(msg)
                    }
                    space()
                }
                .append(model.createdAt?.timeAgo())
        }
    }

    private fun presentReviewRequest(model: ReviewRequestedModel) {
        itemView.apply {
            stateIcon.setImageResource(R.drawable.ic_track_changes)
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append("requested review from ")
                .bold(model.reviewer?.login ?: "")
                .space()
                .append(model.createdAt?.timeAgo())
        }
    }

    private fun presentHeadDeleted(model: HeadRefDeletedModel) {
        itemView.apply {
            stateIcon.setImageResource(R.drawable.ic_track_changes)
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append("deleted ${model.headRefName ?: "this branch"}")
                .space()
                .append(model.createdAt?.timeAgo())
        }
    }

    private fun presentRefRestored(model: HeadRefRestoredModel) {
        itemView.apply {
            stateIcon.setImageResource(R.drawable.ic_track_changes)
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append("restored this branch")
                .space()
                .append(model.createdAt?.timeAgo())
        }
    }

    private fun presentForcePush(model: BaseRefForcePushModel) {
        itemView.apply {
            stateIcon.setImageResource(R.drawable.ic_track_changes)
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append("force pushed this branch from ${model.beforeCommit} to ${model.afterCommit}")
                .space()
                .append(model.createdAt?.timeAgo())
        }
    }

    private fun presentBaseRefChanged(model: BaseRefChangedModel) {
        itemView.apply {
            stateIcon.setImageResource(R.drawable.ic_track_changes)
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append("changed base reference ")
                .space()
                .append(model.createdAt?.timeAgo())
        }
    }

    private fun presentTransferred(model: TransferredEventModel) {
        itemView.apply {
            stateIcon.setImageResource(R.drawable.ic_track_changes)
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append("transferred this from ")
                .bold(model.fromRepository)
                .space()
                .append(model.createdAt?.timeAgo())
        }
    }

    private fun presentRenamed(model: RenamedEventModel) {
        itemView.apply {
            stateIcon.setImageResource(R.drawable.ic_edit)
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append("renamed this from ")
                .bold(model.previousTitle)
                .append(" to ")
                .bold(model.currentTitle)
                .space()
                .append(model.createdAt?.timeAgo())
        }
    }

    private fun presentMilestoneDemilestone(model: MilestoneDemilestonedEventModel) {
        itemView.apply {
            stateIcon.setImageResource(R.drawable.ic_milestone)
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append(if (model.isMilestone == true) "added this to the milestone " else "demilestoned this from ")
                .bold(model.milestoneTitle)
                .space()
                .append(model.createdAt?.timeAgo())
        }
    }

    private fun presentAssignedAndUnassigned(model: AssignedUnAssignedEventModel) {
        itemView.apply {
            stateIcon.setImageResource(R.drawable.ic_profile)
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append(if (model.isAssigned == true) "assigned " else "unassigned ")
                .apply {
                    model.users.forEachIndexed { index, shortUserModel ->
                        bold(shortUserModel.login).append(
                            when {
                                index == model.users.size - 2 -> " and "
                                index != model.users.size - 1 -> ", "
                                else -> " "
                            }
                        )
                    }
                }
                .append(model.createdAt?.timeAgo())
        }
    }

    private fun presentSubscribeUnsubscribed(model: SubscribedUnsubscribedEventModel) {
        itemView.apply {
            stateIcon.setImageResource(if (model.isSubscribe == true) R.drawable.ic_subscribe else R.drawable.ic_unsubscribe)
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append(if (model.isSubscribe == true) "subscribed to this " else "unsubscribed from this ")
                .append(model.createdAt?.timeAgo())
        }
    }

    private fun presentLabels(model: LabelUnlabeledEventModel) {
        itemView.apply {
            stateIcon.setImageResource(R.drawable.ic_label)
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            val builder = SpannableBuilder.builder()
                .apply {
                    bold(SpannableBuilder.builder().append(model.actor?.login ?: "", LabelSpan(Color.TRANSPARENT)))
                    space()
                    append(if (model.isLabel == true) "added " else "unlabeled ", LabelSpan(Color.TRANSPARENT))
                    model.labels.forEach {
                        append(it.name ?: "", LabelSpan(kotlin.runCatching { Color.parseColor("#${it.color}") }.getOrDefault(Color.BLUE)))
                            .space()
                    }
                    append("${model.createdAt?.timeAgo()}", LabelSpan(Color.TRANSPARENT))
                }
            text.text = builder
        }
    }

    @SuppressLint("DefaultLocale")
    private fun presentLockUnlock(model: LockUnlockEventModel) {
        itemView.apply {
            stateIcon.setImageResource(if (model.isLock == true) R.drawable.ic_lock else R.drawable.ic_unlock)
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            val builder = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
            if (model.isLock == true) {
                builder.append("locked ")
                    .bold(if (model.lockReason != null) "as ${model.lockReason?.replace("_", "")?.toLowerCase()} " else "")
                    .append("and limited conversation to collaborators ")
            } else {
                builder.append("unlocked this conversation ")
            }
            text.text = builder.append(model.createdAt?.timeAgo())
        }
    }

    private fun presentClosedReopen(model: CloseOpenEventModel) {
        itemView.apply {
            stateIcon.setImageResource(
                when {
                    PullRequestState.MERGED.rawValue().equals(model.pullRequest?.state, false) -> R.drawable.ic_issue_merged
                    model.isClosed == true -> R.drawable.ic_issue_closed
                    else -> R.drawable.ic_issue_opened
                }
            )
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            val builder = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
            when {
                PullRequestState.MERGED.rawValue().equals(model.pullRequest?.state, false) -> builder
                    .append("merged this ")
                    .append("${model.createdAt?.timeAgo()}")
                model.isClosed == true -> builder
                    .append("closed this ")
                    .clickable(
                        when {
                            model.commit != null -> "in commit (${model.commit?.abbreviatedOid}) "
                            model.pullRequest != null -> "in pull request (#${model.pullRequest?.number}) "
                            else -> ""
                        }
                    )
                    .append("${model.createdAt?.timeAgo()}")
                else -> builder.append("reopened this  ${model.createdAt?.timeAgo()}")
            }
            text.text = builder
        }
    }

    private fun presentCrossReference(model: CrossReferencedEventModel) {
        itemView.apply {
            stateIcon.setImageResource(R.drawable.ic_format_quote)
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append("${if (model.isCrossRepository == true) "cross" else ""} referenced this in ")
                .clickable(
                    when {
                        model.issue != null -> "issue (#${model.issue?.number}) "
                        model.pullRequest != null -> "pull request (#${model.pullRequest?.number}) "
                        else -> ""
                    }
                )
                .append("${model.referencedAt?.timeAgo()}")
        }
    }

    private fun presentCommit(model: CommitModel) {
        itemView.apply {
            stateIcon.setImageResource(R.drawable.ic_push)
            userIcon.loadAvatar(model.author?.avatarUrl, model.author?.url)
            text.text = SpannableBuilder.builder()
                .append(model.author?.name)
                .space()
                .append("referenced this in ")
                .clickable("commit ${model.abbreviatedOid} ")
                .append(model.authoredDate?.timeAgo())
        }
    }

    private fun presentReference(model: ReferencedEventModel) {
        itemView.apply {
            stateIcon.setImageResource(R.drawable.ic_format_quote)
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append("added a commit ")
                .clickable("(${model.commit?.abbreviatedOid})")
                .append(" that referenced this issue ${model.createdAt?.timeAgo()}")
                .newline()
                .bold(model.commit?.message)
        }
    }
}