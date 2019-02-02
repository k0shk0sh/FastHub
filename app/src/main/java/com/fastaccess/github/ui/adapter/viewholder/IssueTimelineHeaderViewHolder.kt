package com.fastaccess.github.ui.adapter.viewholder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import com.fastaccess.data.persistence.models.IssueModel
import com.fastaccess.github.R
import com.fastaccess.github.extensions.getColorAttr
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.markdown.MarkdownProvider
import com.fastaccess.markdown.widget.SpannableBuilder
import kotlinx.android.synthetic.main.issue_header_row_item.view.*
import net.nightwhistler.htmlspanner.HtmlSpanner

/**
 * Created by Kosh on 12.10.18.
 */

class IssueTimelineHeaderViewHolder(
    parent: ViewGroup,
    private val htmlSpanner: HtmlSpanner
) : BaseViewHolder<IssueModel?>(LayoutInflater.from(parent.context)
    .inflate(R.layout.issue_header_row_item, parent, false)) {

    override fun bind(item: IssueModel?) {
        val model = item ?: kotlin.run {
            itemView.isVisible = false
            return
        }
        itemView.apply {
            title.text = model.title
            opener.text = SpannableBuilder.builder()
                .bold(model.author?.login)
                .append(" opened this issue ")
                .append(model.createdAt?.timeAgo())

            userIcon.loadAvatar(model.author?.avatarUrl, model.author?.url ?: "")
            commentName.text = SpannableBuilder.builder()
                .bold(model.author?.login)
                .append(" commented ")
                .append(model.createdAt?.timeAgo())
            MarkdownProvider.loadIntoTextView(htmlSpanner, description, model.bodyHTML ?: "", Color.parseColor("#EEEEEE"), true)
            state.text = model.state?.toLowerCase()
            state.setChipBackgroundColorResource(if ("OPEN" == model.state) {
                R.color.material_green_700
            } else {
                R.color.material_red_700
            })
        }
    }
}