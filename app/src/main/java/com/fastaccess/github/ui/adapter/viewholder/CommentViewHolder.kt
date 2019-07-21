package com.fastaccess.github.ui.adapter.viewholder

import android.annotation.SuppressLint
import android.text.util.Linkify
import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.model.CommentAuthorAssociation
import com.fastaccess.data.model.CommentModel
import com.fastaccess.data.model.getEmoji
import com.fastaccess.github.base.engine.ThemeEngine
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.utils.extensions.popupEmoji
import com.fastaccess.markdown.MarkdownProvider
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.comment_row_item.view.*
import java.util.regex.Pattern


/**
 * Created by Kosh on 12.10.18.
 */

class CommentViewHolder(
    parent: ViewGroup,
    private val markwon: Markwon,
    private val theme: Int,
    private val callback: (position: Int) -> Unit
) : BaseViewHolder<CommentModel?>(
    LayoutInflater.from(parent.context)
        .inflate(com.fastaccess.github.R.layout.comment_row_item, parent, false)
) {

    @SuppressLint("SetTextI18n")
    override fun bind(item: CommentModel?) {
        val model = item ?: run {
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

            MarkdownProvider.loadIntoTextView(
                markwon, description, model.body ?: "", ThemeEngine.getCodeBackground(theme),
                ThemeEngine.isLightTheme(theme)
            )

            val filter = Linkify.TransformFilter { match, _ -> match.group() }

            val mentionPattern = Pattern.compile("@([A-Za-z0-9_-]+)")
            val mentionScheme = "https://www.github.com/"
            Linkify.addLinks(description, mentionPattern, mentionScheme, null, filter)

            val hashtagPattern = Pattern.compile("#([A-Za-z0-9_-]+)")
            val hashtagScheme = "https://www.github.com/"
            Linkify.addLinks(description, hashtagPattern, hashtagScheme, null, filter)

            val urlPattern = Patterns.WEB_URL
            Linkify.addLinks(description, urlPattern, null, null, filter)

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
}