package com.fastaccess.github.ui.adapter.viewholder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.model.CommentAuthorAssociation
import com.fastaccess.data.model.CommentModel
import com.fastaccess.data.model.getEmoji
import com.fastaccess.github.R
import com.fastaccess.github.base.engine.ThemeEngine
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.utils.extensions.popupEmoji
import com.fastaccess.markdown.MarkdownProvider
import com.fastaccess.markdown.spans.drawable.DrawableGetter
import kotlinx.android.synthetic.main.comment_row_item.view.*
import net.nightwhistler.htmlspanner.HtmlSpanner

/**
 * Created by Kosh on 12.10.18.
 */

class CommentViewHolder(
    parent: ViewGroup,
    private val htmlSpanner: HtmlSpanner,
    private val theme: Int,
    private val callback: (position: Int) -> Unit
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

            MarkdownProvider.loadIntoTextView(htmlSpanner, description, model.bodyHTML ?: "", ThemeEngine.getCodeBackground(theme),
                ThemeEngine.isLightTheme(theme))

            addEmoji.setOnClickListener {
                it.popupEmoji(requireNotNull(model.id), model.reactionGroups) {
                    callback.invoke(adapterPosition)
                }
            }

            reactionsText.isVisible = model.reactionGroups?.any { it.users?.totalCount != 0 } ?: false
            if (reactionsText.isVisible) {
                val stringBuilder = StringBuilder()
                model.reactionGroups?.forEach {
                    if (it.users?.totalCount != 0) {
                        stringBuilder.append(it.content.getEmoji())
                            .append(" ")
                            .append("${it.users?.totalCount}")
                            .append("   ")
                    }
                }
                reactionsText.text = stringBuilder
            } else {
                reactionsText.text = ""
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