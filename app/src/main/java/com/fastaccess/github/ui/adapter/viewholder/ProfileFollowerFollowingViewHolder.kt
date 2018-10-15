package com.fastaccess.github.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.persistence.models.FollowingFollowerModel
import com.fastaccess.github.R
import com.fastaccess.github.platform.glide.GlideApp
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.profile_follower_following_row_item.view.*

/**
 * Created by Kosh on 12.10.18.
 */

class ProfileFollowerFollowingViewHolder(parent: ViewGroup) : BaseViewHolder<FollowingFollowerModel>(LayoutInflater.from(parent.context)
        .inflate(R.layout.profile_follower_following_row_item, parent, false)) {

    override fun bind(item: FollowingFollowerModel) {
        itemView.apply {
            title.text = if (item.name.isNullOrEmpty()) item.login else item.name
            description.isVisible = !item.bio.isNullOrEmpty()
            description.text = item.bio
            GlideApp.with(userIcon)
                    .load(item.avatarUrl)
                    .circleCrop()
                    .error(R.drawable.ic_fasthub_mascot)
                    .fallback(R.drawable.ic_fasthub_mascot)
                    .into(userIcon)
        }
    }
}