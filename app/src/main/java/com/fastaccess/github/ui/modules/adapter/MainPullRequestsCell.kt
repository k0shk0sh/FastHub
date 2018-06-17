package com.fastaccess.github.ui.modules.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.fastaccess.data.persistence.models.MainIssuesPullsModel
import com.fastaccess.github.R
import com.jaychang.srv.kae.SimpleCell
import github.type.PullRequestState
import kotlinx.android.synthetic.main.issues_prs_main_screen_row_item.view.*

/**
 * Created by Kosh on 17.06.18.
 */
class MainPullRequestsCell(private val node: MainIssuesPullsModel) : SimpleCell<MainIssuesPullsModel>(node) {

    override fun getLayoutRes(): Int = R.layout.issues_prs_main_screen_row_item

    override fun onBindViewHolder(holder: com.jaychang.srv.kae.SimpleViewHolder, position: Int, context: Context, payload: Any?) {
        holder.itemView.let {
            it.issueTitle.text = "${node.title}#${node.number}"
            it.issueDescription.text = node.repoName
            it.commentCount.isVisible = node.commentCounts != 0L
            it.commentCount.text = "${node.commentCounts ?: 0}"
            it.state.isVisible = true
            it.state.text = node.state?.toLowerCase()?.capitalize() ?: ""
            it.state.setTextColor(when {
                PullRequestState.OPEN.name == node.state -> ContextCompat.getColor(context, R.color.material_green_700)
                PullRequestState.CLOSED.name == node.state -> ContextCompat.getColor(context, R.color.material_red_700)
                PullRequestState.MERGED.name == node.state -> ContextCompat.getColor(context, R.color.material_indigo_700)
                PullRequestState.`$UNKNOWN`.name == node.state -> ContextCompat.getColor(context, R.color.material_green_700)
                else -> ContextCompat.getColor(context, R.color.material_green_700)
            })
        }
    }
}