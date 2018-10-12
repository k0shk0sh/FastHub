package com.fastaccess.github.ui.adapter.viewholder

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.fastaccess.data.persistence.models.ProfileRepoModel
import com.fastaccess.github.R
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.utils.extensions.formatNumber
import kotlinx.android.synthetic.main.profile_pinned_repo_row_item.view.*

/**
 * Created by Kosh on 12.10.18.
 */

class ReposProfileViewHolder(parent: ViewGroup) : BaseViewHolder<ProfileRepoModel>(LayoutInflater.from(parent.context)
        .inflate(R.layout.profile_pinned_repo_row_item, parent, false)) {

    override fun bind(item: ProfileRepoModel) {
        itemView.apply {
            title.text = item.name
            star.text = item.stargazers?.totalCount?.formatNumber() ?: "0"
            forks.text = item.forkCount?.formatNumber() ?: "0"
            issues.text = item.issues?.totalCount?.formatNumber() ?: "0"
            pulls.text = item.pullRequests?.totalCount?.formatNumber() ?: "0"
            language.text = item.primaryLanguage?.name ?: ""
            if (!item.primaryLanguage?.color.isNullOrBlank()) {
                language.chipIconTint = ColorStateList.valueOf(Color.parseColor(item.primaryLanguage?.color)) ?: null
            }
        }
    }
}