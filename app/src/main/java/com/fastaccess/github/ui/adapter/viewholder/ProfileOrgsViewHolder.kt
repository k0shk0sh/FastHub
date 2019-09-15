package com.fastaccess.github.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fastaccess.data.persistence.models.UserOrganizationNodesModel
import com.fastaccess.github.R
import com.fastaccess.github.base.adapter.BaseViewHolder
import kotlinx.android.synthetic.main.icon_row_item.view.*

/**
 * Created by Kosh on 2018-11-17.
 */
class ProfileOrgsViewHolder(parent: ViewGroup) : BaseViewHolder<UserOrganizationNodesModel>(LayoutInflater.from(parent.context)
    .inflate(R.layout.icon_row_item, parent, false)) {

    override fun bind(item: UserOrganizationNodesModel) {
        itemView.imageIcon.loadAvatar(item.avatarUrl, null)
    }

}