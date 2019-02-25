package com.fastaccess.github.ui.adapter.viewholder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.fastaccess.data.model.*
import com.fastaccess.github.R
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.markdown.spans.LabelSpan
import com.fastaccess.markdown.widget.SpannableBuilder
import kotlinx.android.synthetic.main.issue_content_row_item.view.*

/**
 * Created by Kosh on 04.02.19.
 */
class IssueContentViewHolder(parent: ViewGroup) : BaseViewHolder<TimelineModel>(LayoutInflater.from(parent.context)
    .inflate(R.layout.issue_content_row_item, parent, false)) {
    override fun canDivide(): Boolean = false

    override fun bind(item: TimelineModel) {
        itemView.text.text = ""
        itemView.userIcon.setImageDrawable(null)
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
    }

    private fun presentTransferred(model: TransferredEventModel) {
        itemView.apply {
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
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append(if (model.isAssigned == true) "assigned " else "unassigned ")
                .apply {
                    model.users.forEachIndexed { index, shortUserModel ->
                        bold(shortUserModel.login).append(when {
                            index == model.users.size - 2 -> " and "
                            index != model.users.size - 1 -> ", "
                            else -> " "
                        })
                    }
                }
                .append(model.createdAt?.timeAgo())
        }
    }

    private fun presentSubscribeUnsubscribed(model: SubscribedUnsubscribedEventModel) {
        itemView.apply {
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

    private fun presentLockUnlock(model: LockUnlockEventModel) {
        itemView.apply {
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
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            val builder = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
            if (model.isClosed == true) {
                builder
                    .append("closed this ")
                    .clickable(when {
                        model.commit != null -> "in commit (${model.commit?.abbreviatedOid}) "
                        model.pullRequest != null -> "in pull request (#${model.pullRequest?.number}) "
                        else -> ""
                    })
                    .append("${model.createdAt?.timeAgo()}")
            } else {
                builder.append("reopened this  ${model.createdAt?.timeAgo()}")
            }
            text.text = builder
        }
    }

    private fun presentCrossReference(model: CrossReferencedEventModel) {
        itemView.apply {
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append("${if (model.isCrossRepository == true) "cross" else ""} referenced this in ")
                .clickable(when {
                    model.issue != null -> "issue (#${model.issue?.number}) "
                    model.pullRequest != null -> "pull request (#${model.pullRequest?.number}) "
                    else -> ""
                })
                .append("${model.referencedAt?.timeAgo()}")
        }
    }

    private fun presentCommit(model: CommitModel) {
        itemView.apply {
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
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append("added a commit ")
                .clickable("(${model.commit?.abbreviatedOid})")
                .append(" that referenced this issue ${model.createdAt?.timeAgo()}")
                .newline()
                .append(model.commit?.message)
        }
    }
}