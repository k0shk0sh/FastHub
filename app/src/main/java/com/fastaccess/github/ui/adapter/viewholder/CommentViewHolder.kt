package com.fastaccess.github.ui.adapter.viewholder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.model.CommentAuthorAssociation
import com.fastaccess.data.model.CommentModel
import com.fastaccess.github.R
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.showYesNoDialog
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.utils.extensions.popMenu
import io.noties.markwon.Markwon
import io.noties.markwon.utils.NoCopySpannableFactory
import kotlinx.android.synthetic.main.comment_row_item.view.*


/**
 * Created by Kosh on 12.10.18.
 */

class CommentViewHolder(
    parent: ViewGroup,
    private val markwon: Markwon,
    private val theme: Int,
    private val callback: (position: Int) -> Unit,
    private val deleteCommentListener: (position: Int, comment: CommentModel) -> Unit,
    private val editCommentListener: (position: Int, comment: CommentModel) -> Unit
) : BaseViewHolder<CommentModel?>(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.comment_row_item, parent, false)
) {

    @SuppressLint("SetTextI18n", "DefaultLocale")
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
                com.fastaccess.markdown.widget.SpannableBuilder.builder()
                    .bold(model.authorAssociation?.value?.toLowerCase()?.replace("_", "") ?: "")
                    .space()
                    .append(model.updatedAt?.timeAgo())
            }

            description.post {
                description.setSpannableFactory(NoCopySpannableFactory.getInstance())
                val bodyMd = model.body
                markwon.setMarkdown(description, if (!bodyMd.isNullOrEmpty()) bodyMd else resources.getString(R.string.no_description_provided))
            }

            description.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP && !description.hasSelection()) {
                    itemView.callOnClick()
                }
                return@setOnTouchListener false
            }

            val canAlter = model.viewerCanUpdate == true || model.viewerCanDelete == true
            menu.isVisible = canAlter
            if (canAlter) {
                menu.popMenu(R.menu.comment_menu, { menu ->
                    menu.findItem(R.id.edit)?.isVisible = model.viewerCanUpdate == true
                    menu.findItem(R.id.delete)?.isVisible = model.viewerCanDelete == true
                }) { itemId ->
                    if (itemId == R.id.delete) {
                        context.showYesNoDialog(R.string.delete) {
                            it.isTrue {
                                deleteCommentListener.invoke(adapterPosition, model)
                            }
                        }
                    } else if (itemId == R.id.edit) {
                        editCommentListener.invoke(adapterPosition, model)
                    }
                }
            }


//            val filter = Linkify.TransformFilter { match, _ -> match.group() }
//            val mentionPattern = Pattern.compile("@([A-Za-z0-9_-]+)")
//            val mentionScheme = "https://www.github.com/"
//            Linkify.addLinks(description, mentionPattern, mentionScheme, null, filter)
//
//            val hashtagPattern = Pattern.compile("#([A-Za-z0-9_-]+)")
//            val hashtagScheme = "https://www.github.com/"
//            Linkify.addLinks(description, hashtagPattern, hashtagScheme, null, filter)
//
//            val urlPattern = Patterns.WEB_URL
//            Linkify.addLinks(description, urlPattern, null, null, filter)

            adaptiveEmoticon.init(requireNotNull(model.id), model.reactionGroups) {
                callback.invoke(adapterPosition)
            }
        }
    }
}