package com.fastaccess.github.ui.adapter.viewholder

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.persistence.models.ProfileStarredRepoModel
import com.fastaccess.github.R
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.markdown.widget.SpannableBuilder
import com.fastaccess.markdown.spans.LabelSpan
import com.fastaccess.github.extensions.formatNumber
import com.fastaccess.github.extensions.getColorCompat
import kotlinx.android.synthetic.main.profile_pinned_repo_row_item.view.*

/**
 * Created by Kosh on 12.10.18.
 */

class ReposStarredProfileViewHolder(parent: ViewGroup) : BaseViewHolder<ProfileStarredRepoModel>(LayoutInflater.from(parent.context)
        .inflate(R.layout.profile_pinned_repo_row_item, parent, false)) {

    override fun bind(item: ProfileStarredRepoModel) {
        itemView.apply {
            title.text = when {
                item.isPrivate == true -> SpannableBuilder.builder()
                        .append(" ${context.getString(R.string.private_repo)} ", LabelSpan(context.getColorCompat(R.color.material_grey_700)))
                        .space()
                        .append(item.nameWithOwner ?: "", LabelSpan(Color.TRANSPARENT))
                item.isFork == true -> SpannableBuilder.builder()
                        .append(" ${context.getString(R.string.forked)} ", LabelSpan(context.getColorCompat(R.color.material_indigo_700)))
                        .space()
                        .append(item.nameWithOwner ?: "", LabelSpan(Color.TRANSPARENT))
                else -> item.nameWithOwner
            }
            star.text = item.stargazers?.totalCount?.formatNumber() ?: "0"
            forks.text = item.forkCount?.formatNumber() ?: "0"
            issues.text = item.issues?.totalCount?.formatNumber() ?: "0"
            pulls.text = item.pullRequests?.totalCount?.formatNumber() ?: "0"
            language.isVisible = !item.primaryLanguage?.name.isNullOrEmpty()
            language.text = item.primaryLanguage?.name ?: ""
            if (!item.primaryLanguage?.color.isNullOrBlank()) {
                language.chipIconTint = ColorStateList.valueOf(Color.parseColor(item.primaryLanguage?.color)) ?: null
            }
        }
    }
}