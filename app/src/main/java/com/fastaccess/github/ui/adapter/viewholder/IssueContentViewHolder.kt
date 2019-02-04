package com.fastaccess.github.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fastaccess.data.model.CommitModel
import com.fastaccess.data.model.ReferencedEventModel
import com.fastaccess.data.model.TimelineModel
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
        item.commit?.let(this::presentCommit)
        item.referencedEventModel?.let(this::presentReference)

    }

    private fun presentCommit(model: CommitModel) {
        itemView.apply {
            userIcon.loadAvatar(model.author?.avatarUrl, model.author?.url)
            text.text = SpannableBuilder.builder()
                .append(model.author?.name)
                .space()
                .append("referenced this in commit ${model.abbreviatedOid} on ${model.authoredDate?.timeAgo()}")
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
                .append(" that referenced this issue on ${model.createdAt?.timeAgo()}")
                .newline()
                .append(model.commit?.message)
        }
    }
}