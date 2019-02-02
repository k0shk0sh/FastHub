package com.fastaccess.github.ui.adapter.viewholder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.persistence.models.MyIssuesPullsModel
import com.fastaccess.github.R
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.extensions.getColorCompat
import github.type.PullRequestState
import kotlinx.android.synthetic.main.issues_prs_main_screen_row_item.view.*

class MyIssuesPrsViewHolder(viewGroup: ViewGroup) : BaseViewHolder<MyIssuesPullsModel?>(LayoutInflater.from(viewGroup.context)
    .inflate(R.layout.issues_prs_main_screen_row_item, viewGroup, false)) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    @SuppressLint("SetTextI18n")
    override fun bind(model: MyIssuesPullsModel?) {
        itemView.let {
            val item = model ?: kotlin.run {
                itemView.isVisible = false
                return
            }
            it.issueTitle.text = "${item.title}#${item.number}"
            it.issueDescription.apply {
                text = item.repoName ?: ""
                isVisible = !item.repoName.isNullOrBlank()
            }
            it.commentCount.isVisible = item.commentCounts != 0
            it.commentCount.text = "${item.commentCounts ?: 0}"
            if (!item.state.isNullOrEmpty()) { // PR
                it.state.isVisible = true
                it.state.text = item.state?.toLowerCase()?.capitalize() ?: ""
                it.state.setTextColor(when {
                    PullRequestState.OPEN.name == item.state -> it.context.getColorCompat(R.color.material_green_700)
                    PullRequestState.CLOSED.name == item.state -> it.context.getColorCompat(R.color.material_red_700)
                    PullRequestState.MERGED.name == item.state -> it.context.getColorCompat(R.color.material_indigo_700)
                    PullRequestState.`$UNKNOWN`.name == item.state -> it.context.getColorCompat(R.color.material_green_700)
                    else -> it.context.getColorCompat(R.color.material_green_700)
                })
            }
        }
    }
}