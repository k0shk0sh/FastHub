package com.fastaccess.github.ui.adapter.viewholder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.persistence.models.IssueModel
import com.fastaccess.github.R
import com.fastaccess.github.base.engine.ThemeEngine
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.markdown.MarkdownProvider
import com.fastaccess.markdown.spans.drawable.DrawableGetter
import com.fastaccess.markdown.widget.SpannableBuilder
import kotlinx.android.synthetic.main.issue_header_row_item.view.*
import net.nightwhistler.htmlspanner.HtmlSpanner

/**
 * Created by Kosh on 12.10.18.
 */

class IssueTimelineHeaderViewHolder(
    parent: ViewGroup,
    private val htmlSpanner: HtmlSpanner,
    private val theme: Int,
    private val callback: (position: Int) -> Unit
) : BaseViewHolder<IssueModel?>(LayoutInflater.from(parent.context)
    .inflate(R.layout.issue_header_row_item, parent, false)) {

    @SuppressLint("SetTextI18n")
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
            MarkdownProvider.loadIntoTextView(htmlSpanner, description, model.bodyHTML ?: "", ThemeEngine.getCodeBackground(theme),
                ThemeEngine.isLightTheme(theme))
            state.text = model.state?.toLowerCase()
            state.setChipBackgroundColorResource(if ("OPEN" == model.state) {
                R.color.material_green_700
            } else {
                R.color.material_red_700
            })
            reactionGroup.setup(model.id, model.reactionGroups) {
                callback.invoke(adapterPosition)
            }
        }
    }

    override fun onDetached() {
        super.onDetached()
        itemView.description?.let {
            if (it.tag is DrawableGetter) {
                val target = it.tag as DrawableGetter
                target.clear(target)
            }
        }
    }
}