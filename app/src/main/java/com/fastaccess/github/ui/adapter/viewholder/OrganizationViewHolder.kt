package com.fastaccess.github.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fastaccess.data.persistence.models.OrganizationModel
import com.fastaccess.github.R
import com.fastaccess.github.extensions.formatNumber
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.organization_row_item.view.*

/**
 * Created by Kosh on 12.10.18.
 */

class OrganizationViewHolder(parent: ViewGroup) : BaseViewHolder<OrganizationModel>(LayoutInflater.from(parent.context)
    .inflate(R.layout.organization_row_item, parent, false)) {

    override fun bind(item: OrganizationModel) {
        itemView.apply {
            title.text = if (item.name.isNullOrEmpty()) item.login else item.name
            avatar.loadAvatar(item.avatarUrl, item.url)
            teams.text = item.teams?.totalCount?.formatNumber() ?: "0"
            members.text = item.members?.totalCount?.formatNumber() ?: "0"
            projects.text = item.projects?.totalCount?.formatNumber() ?: "0"
            repos.text = item.repositories?.totalCount?.formatNumber() ?: "0"
        }
    }
}