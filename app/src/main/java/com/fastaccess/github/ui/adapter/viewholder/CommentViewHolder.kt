package com.fastaccess.github.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.model.CommentModel
import com.fastaccess.github.R
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.comment_row_item.view.*
import ru.noties.markwon.Markwon

/**
 * Created by Kosh on 12.10.18.
 */

class CommentViewHolder(parent: ViewGroup) : BaseViewHolder<CommentModel?>(LayoutInflater.from(parent.context)
    .inflate(R.layout.comment_row_item, parent, false)) {

    override fun bind(model: CommentModel?) {
        val item = model ?: kotlin.run {
            itemView.isVisible = false
            return
        }
        itemView.apply {
            userIcon.loadAvatar(item.author?.avatarUrl, item.author?.url ?: "")
            author.text = item.author?.login
            association.text = "${if (item.authorAssociation != null) item.authorAssociation?.value?.replace("_", "")?.toLowerCase() else ""}" +
                " ${item.updatedAt?.timeAgo()}"
            description.text = Markwon.markdown(context, item.body ?: "")
        }
    }
}