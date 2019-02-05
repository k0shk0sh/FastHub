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
    }

    private fun presentLabels(model: LabelUnlabeledEventModel) {
        itemView.apply {
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            val builder = SpannableBuilder.builder()
                .apply {
                    bold(SpannableBuilder.builder().append(model.actor?.login ?: "", LabelSpan(Color.TRANSPARENT)))
                    space()
                    append("${if (model.isLabel == true) "added " else "unlabeled "}", LabelSpan(Color.TRANSPARENT))
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
                builder.append("locked as ")
                    .bold("${model.lockReason?.replace("_", "")?.toLowerCase()} ")
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
                    .bold(when {
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
                .bold(when {
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
                .append("referenced this in commit ${model.abbreviatedOid} ${model.authoredDate?.timeAgo()}")
        }
    }

    private fun presentReference(model: ReferencedEventModel) {
        itemView.apply {
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append("added a commit ")
                .bold("(${model.commit?.abbreviatedOid})")
                .append(" that referenced this issue ${model.createdAt?.timeAgo()}")
                .newline()
                .append(model.commit?.message)
        }
    }
}