package com.fastaccess.github.ui.adapter.viewholder

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.model.CommentAuthorAssociation
import com.fastaccess.data.model.CommentModel
import com.fastaccess.data.model.ReactionContent
import com.fastaccess.github.R
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.markdown.MarkdownProvider
import com.fastaccess.markdown.extension.*
import com.fastaccess.markdown.spans.drawable.DrawableGetter
import kotlinx.android.synthetic.main.comment_row_item.view.*
import kotlinx.android.synthetic.main.reactions_chips_layout.view.*
import net.nightwhistler.htmlspanner.HtmlSpanner

/**
 * Created by Kosh on 12.10.18.
 */

class CommentViewHolder(
    parent: ViewGroup,
    private val htmlSpanner: HtmlSpanner
) : BaseViewHolder<CommentModel?>(LayoutInflater.from(parent.context)
    .inflate(R.layout.comment_row_item, parent, false)) {

    @SuppressLint("SetTextI18n")
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
            MarkdownProvider.loadIntoTextView(htmlSpanner, description, model.bodyHTML ?: "", Color.parseColor("#EEEEEE"), true)

            thumbsUp.text = "${getThumbsUpEmoji()} ${model.reactionGroups?.firstOrNull { it.content == ReactionContent.THUMBS_UP }?.users?.totalCount}"
            thumbsDown.text = "${getThumbsDownEmoji()} ${model.reactionGroups?.firstOrNull { it.content == ReactionContent.THUMBS_DOWN }?.users?.totalCount}"
            laugh.text = "${getLaughEmoji()} ${model.reactionGroups?.firstOrNull { it.content == ReactionContent.LAUGH }?.users?.totalCount}"
            hooray.text = "${getHoorayEmoji()} ${model.reactionGroups?.firstOrNull { it.content == ReactionContent.HOORAY }?.users?.totalCount}"
            heart.text = "${getHeartEmoji()} ${model.reactionGroups?.firstOrNull { it.content == ReactionContent.HEART }?.users?.totalCount}"
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