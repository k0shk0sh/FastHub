package com.fastaccess.github.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fastaccess.data.model.*
import com.fastaccess.github.R
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
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
        when {
            item.commit != null -> presentCommit(item.commit)
            item.crossReferencedEventModel != null -> presentCrossReference(item.crossReferencedEventModel)
            item.referencedEventModel != null -> presentReference(item.referencedEventModel)
            item.closeOpenEventModel != null -> presentClosedReopen(item.closeOpenEventModel)
        }
    }

    private fun presentClosedReopen(model: CloseOpenEventModel?) {
        if (model == null) {
            return
        }
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
                        model.pullRequest != null -> "in pull request (${model.pullRequest?.number}) "
                        else -> ""
                    })
                    .append("${model.createdAt?.timeAgo()}")
            } else {
                builder.append("reopened this  ${model.createdAt?.timeAgo()}")
            }
            text.text = builder
        }
    }

    private fun presentCrossReference(model: CrossReferencedEventModel?) {
        if (model == null) {
            return
        }
        itemView.apply {
            userIcon.loadAvatar(model.actor?.avatarUrl, model.actor?.url)
            text.text = SpannableBuilder.builder()
                .bold(model.actor?.login)
                .space()
                .append("${if (model.isCrossRepository == true) "cross" else ""} referenced this in ")
                .bold(when {
                    model.issue != null -> "issue (${model.issue?.number}) "
                    model.pullRequest != null -> "pull request (${model.pullRequest?.number}) "
                    else -> ""
                })
                .append("${model.referencedAt?.timeAgo()}")
        }
    }

    private fun presentCommit(model: CommitModel?) {
        if (model == null) {
            return
        }
        itemView.apply {
            userIcon.loadAvatar(model.author?.avatarUrl, model.author?.url)
            text.text = SpannableBuilder.builder()
                .append(model.author?.name)
                .space()
                .append("referenced this in commit ${model.abbreviatedOid} ${model.authoredDate?.timeAgo()}")
        }
    }

    private fun presentReference(model: ReferencedEventModel?) {
        if (model == null) {
            return
        }
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