package com.fastaccess.github.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import com.fastaccess.data.model.CommentAuthorAssociation
import com.fastaccess.data.model.CommentModel
import com.fastaccess.github.R
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.comment_row_item.view.*

/**
 * Created by Kosh on 12.10.18.
 */

class CommentViewHolder(parent: ViewGroup) : BaseViewHolder<CommentModel?>(LayoutInflater.from(parent.context)
    .inflate(R.layout.comment_row_item, parent, false)) {

    override fun bind(item: CommentModel?) {
        val model = item ?: kotlin.run {
            itemView.isVisible = false
            return
        }
        itemView.apply {
            userIcon.loadAvatar(model.author?.avatarUrl, model.author?.url ?: "")
            author.text = model.author?.login ?: ""
            association.text = if (CommentAuthorAssociation.NONE == model.authorAssociation) {
                model.updatedAt?.timeAgo()
            } else {
                "${model.authorAssociation?.value?.toLowerCase()?.replace("_", "")} ${model.updatedAt?.timeAgo()}"
            }
            description.text = HtmlCompat.fromHtml(item.bodyHTML ?: "", HtmlCompat.FROM_HTML_OPTION_USE_CSS_COLORS)
        }
    }
}