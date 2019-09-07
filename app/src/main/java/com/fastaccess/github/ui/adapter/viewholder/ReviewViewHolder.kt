package com.fastaccess.github.ui.adapter.viewholder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.model.CommentAuthorAssociation
import com.fastaccess.data.model.ReviewModel
import com.fastaccess.github.R
import com.fastaccess.github.extensions.getColorAttr
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.showYesNoDialog
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.utils.extensions.popMenu
import com.fastaccess.markdown.spans.DiffLineSpan
import com.fastaccess.markdown.widget.SpannableBuilder
import io.noties.markwon.Markwon
import io.noties.markwon.utils.NoCopySpannableFactory
import kotlinx.android.synthetic.main.comment_small_row_item.view.*
import kotlinx.android.synthetic.main.review_with_comment_row_item.view.*


/**
 * Created by Kosh on 12.10.18.
 */

class ReviewViewHolder(
    parent: ViewGroup,
    private val markwon: Markwon,
    private val theme: Int,
    private val callback: (position: Int) -> Unit,
    private val deleteCommentListener: (position: Int) -> Unit,
    private val editCommentListener: (position: Int) -> Unit
) : BaseViewHolder<ReviewModel?>(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.review_with_comment_row_item, parent, false)
) {

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun bind(item: ReviewModel?) {
        val review = item ?: run {
            itemView.isVisible = false
            return
        }
        itemView.apply {
            itemView.commentLayout.isVisible = review.comment != null
            review.comment?.let { model ->
                fileName.text = model.path
                if (!model.diffHunk.isNullOrEmpty()) {
                    diffHunk.text = DiffLineSpan.getSpannable(
                        model.diffHunk,
                        context.getColorAttr(R.attr.patch_addition), context.getColorAttr(R.attr.patch_deletion),
                        context.getColorAttr(R.attr.patch_ref),
                        truncate = true
                    )
                    diffHunk.isVisible = true
                } else {
                    diffHunk.isVisible = false
                }
                userIcon.loadAvatar(model.author?.avatarUrl, model.author?.url ?: "")
                author.text = model.author?.login ?: ""
                association.text = if (CommentAuthorAssociation.NONE == model.authorAssociation) {
                    model.updatedAt?.timeAgo()
                } else {
                    SpannableBuilder.builder()
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
                                    deleteCommentListener.invoke(adapterPosition)
                                }
                            }
                        } else if (itemId == R.id.edit) {
                            editCommentListener.invoke(adapterPosition)
                        }
                    }
                }

                adaptiveEmoticon.init(requireNotNull(model.id), model.reactionGroups) {
                    callback.invoke(adapterPosition)
                }
            }
        }
    }
}