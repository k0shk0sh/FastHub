package com.fastaccess.github.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fastaccess.data.persistence.models.ProfileGistModel
import com.fastaccess.github.R
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.extensions.formatNumber
import kotlinx.android.synthetic.main.profile_gist_repo_row_item.view.*

/**
 * Created by Kosh on 12.10.18.
 */

class ProfileGistsViewHolder(parent: ViewGroup) : BaseViewHolder<ProfileGistModel>(LayoutInflater.from(parent.context)
        .inflate(R.layout.profile_gist_repo_row_item, parent, false)) {

    override fun bind(item: ProfileGistModel) {
        itemView.apply {
            title.text = if (item.description.isNullOrEmpty()) context.getString(R.string.no_data) else item.description
            starCount.text = item.stargazers?.totalCount?.formatNumber() ?: "0"
            commentCount.text = item.comments?.totalCount?.formatNumber() ?: "0"
            if (item.isPublic == true) {
                isPublic.text = context.getString(R.string.public_label)
                isPublic.setChipIconResource(R.drawable.ic_brower)
            } else {
                isPublic.text = context.getString(R.string.private_label)
                isPublic.setChipIconResource(R.drawable.ic_incognito)
            }
        }
    }
}