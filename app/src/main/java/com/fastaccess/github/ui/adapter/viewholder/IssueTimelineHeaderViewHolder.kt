package com.fastaccess.github.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fastaccess.data.persistence.models.IssueModel
import com.fastaccess.github.R
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.ui.widget.SpannableBuilder
import kotlinx.android.synthetic.main.issue_header_row_item.view.*
import ru.noties.markwon.Markwon

/**
 * Created by Kosh on 12.10.18.
 */

class IssueTimelineHeaderViewHolder(parent: ViewGroup) : BaseViewHolder<IssueModel>(LayoutInflater.from(parent.context)
    .inflate(R.layout.issue_header_row_item, parent, false)) {

    override fun bind(item: IssueModel) {
        itemView.apply {
            title.text = item.title
            opener.text = SpannableBuilder.builder()
                .bold(item.author?.login)
                .append(" opened this issue ")
                .append(item.createdAt?.timeAgo())

            userIcon.loadAvatar(item.author?.avatarUrl)
            commentName.text = SpannableBuilder.builder()
                .bold(item.author?.login)
                .append(" commented ")
                .append(item.createdAt?.timeAgo())
            description.text = if (item.bodyHTML.isNullOrEmpty()) {
                context.getString(R.string.no_description_provided)
            } else {
                Markwon.markdown(context, item.bodyHTML ?: "")
            }
            state.text = item.state?.toLowerCase()
            state.setChipBackgroundColorResource(if ("OPEN" == item.state) {
                R.color.material_green_700
            } else {
                R.color.material_red_700
            })
        }
    }
}