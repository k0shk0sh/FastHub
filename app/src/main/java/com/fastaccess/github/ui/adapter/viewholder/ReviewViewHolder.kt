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
import github.type.PullRequestReviewState
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
    private val deleteCommentListener: (position: Int, isReviewBody: Boolean) -> Unit,
    private val editCommentListener: (position: Int, isReviewBody: Boolean) -> Unit
) : BaseViewHolder<ReviewModel?>(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.review_with_comment_row_item, parent, false)
) {

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun bind(item: ReviewModel?) {
        val _review = item ?: run {
            itemView.isVisible = false
            return
        }
        itemView.apply {
            itemView.commentLayout.isVisible = _review.comment != null
            val showReview = !_review.body.isNullOrBlank()
            if (showReview) {
                initReview(_review)
            }
            reviewLayout.isVisible = showReview
            _review.comment?.let { model ->
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
                    if (!showReview) {
                        SpannableBuilder.builder()
                            .bold(_review.state?.replace("_", "")?.toLowerCase())
                            .append(", ")
                            .append(model.createdAt?.timeAgo())
                    } else {
                        model.createdAt?.timeAgo()
                    }
                } else {
                    SpannableBuilder.builder()
                        .bold(model.authorAssociation?.value?.toLowerCase()?.replace("_", "") ?: "")
                        .append(", ")
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
                                    deleteCommentListener.invoke(adapterPosition, false)
                                }
                            }
                        } else if (itemId == R.id.edit) {
                            editCommentListener.invoke(adapterPosition, false)
                        }
                    }
                }

                adaptiveEmoticon.init(requireNotNull(model.id), model.reactionGroups) {
                    callback.invoke(adapterPosition)
                }
            }
            divider.isVisible = _review.comment != null
            reviewCommentLayout.isVisible = _review.comment != null
        }
    }

    @SuppressLint("DefaultLocale")
    private fun initReview(model: ReviewModel) {
        itemView.apply {
            reviewUserIcon.loadAvatar(model.author?.avatarUrl, model.author?.url ?: "")
            reviewAuthor.text = model.author?.login ?: ""
            reviewAssociation.text = if (CommentAuthorAssociation.NONE.value == model.authorAssociation) {
                SpannableBuilder.builder()
                    .bold(model.state?.replace("_", "")?.toLowerCase())
                    .append(", ")
                    .append(model.createdAt?.timeAgo())
            } else {
                SpannableBuilder.builder()
                    .bold(model.state?.replace("_", "")?.toLowerCase())
                    .append(", ")
                    .append(model.authorAssociation?.toLowerCase()?.replace("_", "") ?: "")
                    .append(", ")
                    .append(model.createdAt?.timeAgo())
            }

            reviewDescription.post {
                reviewDescription.setSpannableFactory(NoCopySpannableFactory.getInstance())
                val bodyMd = model.body
                markwon.setMarkdown(reviewDescription, if (!bodyMd.isNullOrEmpty()) bodyMd else resources.getString(R.string.no_description_provided))
            }

            reviewDescription.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP && !description.hasSelection()) {
                    itemView.callOnClick()
                }
                return@setOnTouchListener false
            }

            val canAlter = model.viewerCanUpdate == true || model.viewerCanDelete == true
            reviewMenu.isVisible = canAlter
            if (canAlter) {
                reviewMenu.popMenu(R.menu.comment_menu, { menu ->
                    menu.findItem(R.id.edit)?.isVisible = model.viewerCanUpdate == true
                    menu.findItem(R.id.delete)?.let {
                        it.isVisible = model.viewerCanDelete == true &&
                            model.state != PullRequestReviewState.COMMENTED.rawValue()
                        it.title = context.getString(R.string.dismiss_review)
                    }
                }) { itemId ->
                    if (itemId == R.id.delete) {
                        deleteCommentListener.invoke(adapterPosition, true)
                    } else if (itemId == R.id.edit) {
                        editCommentListener.invoke(adapterPosition, true)
                    }
                }
            }

            adaptiveEmoticon.init(requireNotNull(model.id), model.reactionGroups) {
                callback.invoke(adapterPosition)
            }
        }
    }
}