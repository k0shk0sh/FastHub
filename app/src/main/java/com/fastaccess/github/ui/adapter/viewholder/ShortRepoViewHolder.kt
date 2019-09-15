package com.fastaccess.github.ui.adapter.viewholder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.model.ShortRepoModel
import com.fastaccess.github.R
import com.fastaccess.github.extensions.formatNumber
import com.fastaccess.github.base.adapter.BaseViewHolder
import com.fastaccess.markdown.widget.SpannableBuilder
import com.fastaccess.markdown.spans.LabelSpan
import com.fastaccess.github.extensions.getColorCompat
import kotlinx.android.synthetic.main.profile_pinned_repo_row_item.view.*

/**
 * Created by Kosh on 12.10.18.
 */

class ShortRepoViewHolder(parent: ViewGroup) : BaseViewHolder<ShortRepoModel>(LayoutInflater.from(parent.context)
    .inflate(R.layout.profile_pinned_repo_row_item, parent, false)) {

    override fun bind(item: ShortRepoModel) {
        itemView.apply {
            title.text = when {
                item.isPrivate == true -> SpannableBuilder.builder()
                    .append(" ${context.getString(R.string.private_repo)} ", LabelSpan(context.getColorCompat(R.color.material_grey_700)))
                    .space()
                    .append(item.name ?: "", LabelSpan(Color.TRANSPARENT))
                item.isFork == true -> SpannableBuilder.builder()
                    .append(" ${context.getString(R.string.forked)} ", LabelSpan(context.getColorCompat(R.color.material_indigo_700)))
                    .space()
                    .append(item.name ?: "", LabelSpan(Color.TRANSPARENT))
                else -> item.name
            }
            star.text = item.stargazers?.totalCount?.formatNumber() ?: "0"
            forks.text = item.forkCount?.formatNumber() ?: "0"
            issues.text = item.issues?.totalCount?.formatNumber() ?: "0"
            pulls.text = item.pullRequests?.totalCount?.formatNumber() ?: "0"
            language.isVisible = false
        }
    }
}