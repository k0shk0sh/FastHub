package com.fastaccess.github.ui.adapter.viewholder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.persistence.models.MyIssuesPullsModel
import com.fastaccess.github.R
import com.fastaccess.github.extensions.getColorCompat
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import github.type.PullRequestState
import kotlinx.android.synthetic.main.issues_prs_main_screen_row_item.view.*

class MyIssuesPrsViewHolder(viewGroup: ViewGroup) : BaseViewHolder<MyIssuesPullsModel?>(
    LayoutInflater.from(viewGroup.context)
        .inflate(R.layout.issues_prs_main_screen_row_item, viewGroup, false)
) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    @SuppressLint("SetTextI18n")
    override fun bind(model: MyIssuesPullsModel?) {
        itemView.let {
            val item = model ?: run {
                itemView.isVisible = false
                return
            }
            it.issueTitle.text = "${item.title}#${item.number}"
            it.issueDescription.apply {
                text = item.repoName ?: ""
                isVisible = !item.repoName.isNullOrBlank()
            }
            it.commentCountBox.isVisible = item.commentCounts != 0
            it.commentCount.text = "${if (item.commentCounts ?: 0 > 99) "99" else item.commentCounts}"
            if (!item.state.isNullOrEmpty()) {
                it.stateIcon.isVisible = true
                it.stateIcon.setImageResource(
                    when {
                        PullRequestState.OPEN.name == item.state -> R.drawable.circle_shape_green_small
                        PullRequestState.CLOSED.name == item.state -> R.drawable.circle_shape_red_small
                        PullRequestState.MERGED.name == item.state -> R.drawable.circle_shape_blue_small
                        else -> it.context.getColorCompat(R.color.material_green_700)
                    }
                )
            }
        }
    }
}