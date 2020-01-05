package com.fastaccess.fasthub.reviews.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.model.CommentAuthorAssociation
import com.fastaccess.data.model.ReviewModel
import com.fastaccess.fasthub.reviews.R
import com.fastaccess.github.base.adapter.BaseViewHolder
import com.fastaccess.github.base.extensions.popMenu
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.markdown.widget.SpannableBuilder
import io.noties.markwon.Markwon
import io.noties.markwon.utils.NoCopySpannableFactory
import kotlinx.android.synthetic.main.review_row_item.view.*

class ReviewViewHolder(
    parent: ViewGroup,
    private val markwon: Markwon,
    private val theme: Int,
    private val callback: (position: Int) -> Unit,
    private val editCommentListener: (position: Int) -> Unit
) : BaseViewHolder<ReviewModel>(LayoutInflater.from(parent.context).inflate(R.layout.review_row_item, parent, false)) {

    @SuppressLint("DefaultLocale")
    override fun bind(item: ReviewModel) {
        itemView.apply {
            val hasBody = !item.body.isNullOrEmpty()
            descriptionLayout.isVisible = hasBody
            reviewMenu.isVisible = hasBody
            reviewUserIcon.loadAvatar(item.author?.avatarUrl, item.author?.url ?: "")
            reviewAuthor.text = item.author?.login ?: ""
            reviewAssociation.text = if (CommentAuthorAssociation.NONE.value == item.authorAssociation) {
                SpannableBuilder.builder()
                    .bold(item.state?.replace("_", " ")?.toLowerCase())
                    .append(", ")
                    .append(item.createdAt?.timeAgo())
            } else {
                SpannableBuilder.builder()
                    .bold(item.state?.replace("_", " ")?.toLowerCase())
                    .append(", ")
                    .append(item.authorAssociation?.toLowerCase()?.replace("_", "") ?: "")
                    .append(", ")
                    .append(item.createdAt?.timeAgo())
            }

            if (hasBody) {
                reviewDescription.post {
                    reviewDescription.setSpannableFactory(NoCopySpannableFactory.getInstance())
                    val bodyMd = item.body
                    markwon.setMarkdown(
                        reviewDescription,
                        if (!bodyMd.isNullOrEmpty()) bodyMd else resources.getString(R.string.no_description_provided)
                    )
                }

                reviewDescription.setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_UP && !reviewDescription.hasSelection()) {
                        itemView.callOnClick()
                    }
                    return@setOnTouchListener false
                }

                val canAlter = item.viewerCanUpdate == true || item.viewerCanDelete == true
                reviewMenu.isVisible = canAlter
                if (canAlter) {
                    reviewMenu.popMenu(R.menu.comment_menu, { menu ->
                        menu.findItem(R.id.edit)?.isVisible = item.viewerCanUpdate == true
                        menu.findItem(R.id.delete)?.isVisible = false
                    }) { itemId ->
                        if (itemId == R.id.edit) {
                            editCommentListener.invoke(adapterPosition)
                        }
                    }
                }

                reviewAdaptiveEmoticon.init(requireNotNull(item.id), item.reactionGroups) {
                    callback.invoke(adapterPosition)
                }
            }
        }
    }
}