package com.fastaccess.fasthub.commit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.model.FullCommitModel
import com.fastaccess.fasthub.commit.R
import com.fastaccess.github.base.adapter.BaseViewHolder
import com.fastaccess.github.extensions.timeAgo
import github.type.StatusState
import kotlinx.android.synthetic.main.commit_row_item.view.*

class CommitsViewHolder(parent: ViewGroup) : BaseViewHolder<FullCommitModel>(
    LayoutInflater.from(parent.context).inflate(R.layout.commit_row_item, parent, false)
) {
    override fun bind(item: FullCommitModel) {
        itemView.apply {
            userIcon.loadAvatar(item.author?.avatarUrl, item.author?.url)
            val icon = when (item.state) {
                StatusState.ERROR.rawValue(), StatusState.FAILURE.rawValue() -> R.drawable.ic_state_error
                StatusState.SUCCESS.rawValue() -> R.drawable.ic_state_success
                StatusState.PENDING.rawValue() -> R.drawable.ic_state_pending
                else -> 0
            }
            if (icon == 0) {
                stateIcon.isVisible = false
            } else {
                stateIcon.setImageResource(icon)
                stateIcon.isVisible = true
            }
            if (item.isVerified == true) {
                association.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_verified_small, 0, 0, 0)
            } else {
                association.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            }
            author.text = item.author?.login
            association.text = item.authoredDate?.timeAgo()
            if (!item.messageHeadline.isNullOrEmpty()) {
                description.text = item.messageHeadline
                description.isVisible = true
            } else {
                description.isVisible = false
            }
        }
    }

}