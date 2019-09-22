package com.fastaccess.fasthub.reviews.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.model.CommentAuthorAssociation
import com.fastaccess.data.model.CommentModel
import com.fastaccess.fasthub.reviews.R
import com.fastaccess.github.base.adapter.BaseViewHolder
import com.fastaccess.github.base.extensions.popMenu
import com.fastaccess.github.extensions.getColorAttr
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.showYesNoDialog
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.markdown.spans.DiffLineSpan
import com.fastaccess.markdown.widget.SpannableBuilder
import io.noties.markwon.Markwon
import io.noties.markwon.utils.NoCopySpannableFactory
import kotlinx.android.synthetic.main.review_comment_row_item.view.*

class ReviewCommentViewHolder(
    parent: ViewGroup,
    private val markwon: Markwon,
    private val theme: Int,
    private val callback: (position: Int) -> Unit,
    private val deleteCommentListener: (position: Int) -> Unit,
    private val editCommentListener: (position: Int) -> Unit
) : BaseViewHolder<CommentModel>(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.review_comment_row_item, parent, false)
) {
    @SuppressLint("DefaultLocale")
    override fun bind(item: CommentModel) {
        itemView.apply {
            fileName.text = item.path
            if (!item.diffHunk.isNullOrEmpty()) {
                diffHunk.text = DiffLineSpan.getSpannable(
                    item.diffHunk,
                    context.getColorAttr(R.attr.patch_addition), context.getColorAttr(R.attr.patch_deletion),
                    context.getColorAttr(R.attr.patch_ref),
                    truncate = true
                )
                diffHunk.isVisible = true
            } else {
                diffHunk.isVisible = false
            }
            userIcon.loadAvatar(item.author?.avatarUrl, item.author?.url ?: "")
            author.text = item.author?.login ?: ""
            association.text = if (CommentAuthorAssociation.NONE == item.authorAssociation) {
                item.createdAt?.timeAgo()
            } else {
                SpannableBuilder.builder()
                    .bold(item.authorAssociation?.value?.toLowerCase()?.replace("_", "") ?: "")
                    .append(", ")
                    .append(item.updatedAt?.timeAgo())
            }

            description.post {
                description.setSpannableFactory(NoCopySpannableFactory.getInstance())
                val bodyMd = item.body
                markwon.setMarkdown(description, if (!bodyMd.isNullOrEmpty()) bodyMd else resources.getString(R.string.no_description_provided))
            }

            description.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP && !description.hasSelection()) {
                    itemView.callOnClick()
                }
                return@setOnTouchListener false
            }

            val canAlter = item.viewerCanUpdate == true || item.viewerCanDelete == true
            menu.isVisible = canAlter
            if (canAlter) {
                menu.popMenu(R.menu.comment_menu, { menu ->
                    menu.findItem(R.id.edit)?.isVisible = item.viewerCanUpdate == true
                    menu.findItem(R.id.delete)?.isVisible = item.viewerCanDelete == true
                }) { itemId ->
                    if (itemId == R.id.delete) {
                        context.showYesNoDialog(R.string.delete) {
                            it.isTrue {
                                deleteCommentListener.invoke(adapterPosition)
                            }
                        }
                    } else if (itemId == R.id.edit) {
                        editCommentListener.invoke(adapterPosition)
                    }
                }
            }

            adaptiveEmoticon.init(requireNotNull(item.id), item.reactionGroups) {
                callback.invoke(adapterPosition)
            }
        }
    }
}