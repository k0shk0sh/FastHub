package com.fastaccess.github.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.persistence.models.LoginModel
import com.fastaccess.github.R
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.profile_follower_following_row_item.view.*

/**
 * Created by Kosh on 12.10.18.
 */

class LoggedInUserViewHolder(parent: ViewGroup) : BaseViewHolder<LoginModel>(LayoutInflater.from(parent.context)
        .inflate(R.layout.profile_follower_following_row_item, parent, false)) {

    override fun bind(item: LoginModel) {
        itemView.apply {
            title.text = if (item.name.isNullOrEmpty()) item.login else item.name
            description.isVisible = !item.bio.isNullOrEmpty()
            description.text = item.bio
            userIcon.loadAvatar(item.avatarUrl)
        }
    }
}