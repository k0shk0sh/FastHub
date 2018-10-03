package com.fastaccess.github.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.fastaccess.data.persistence.models.MainIssuesPullsModel
import com.fastaccess.github.R
import com.jaychang.srv.Updatable
import com.jaychang.srv.kae.SimpleCell
import com.jaychang.srv.kae.SimpleViewHolder
import github.type.PullRequestState
import kotlinx.android.synthetic.main.issues_prs_main_screen_row_item.view.*

class MainIssuesPrsCell(private val node: MainIssuesPullsModel) : SimpleCell<MainIssuesPullsModel>(node), Updatable<MainIssuesPullsModel> {
    override fun getLayoutRes(): Int = R.layout.issues_prs_main_screen_row_item

    override fun areContentsTheSame(newItem: MainIssuesPullsModel): Boolean = node == newItem

    override fun getChangePayload(newItem: MainIssuesPullsModel): Any = newItem

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int, context: Context, payload: Any?) {
        holder.itemView.let {
            val item = payload as? MainIssuesPullsModel ?: item
            it.issueTitle.text = "${item.title}#${item.number}"
            it.issueDescription.apply {
                text = item.repoName ?: ""
                isVisible = !item.repoName.isNullOrBlank()
            }
            it.commentCount.isVisible = item.commentCounts != 0L
            it.commentCount.text = "${item.commentCounts ?: 0}"
            if (!item.state.isNullOrEmpty()) { // PR
                it.state.isVisible = true
                it.state.text = item.state?.toLowerCase()?.capitalize() ?: ""
                it.state.setTextColor(when {
                    PullRequestState.OPEN.name == item.state -> ContextCompat.getColor(context, R.color.material_green_700)
                    PullRequestState.CLOSED.name == item.state -> ContextCompat.getColor(context, R.color.material_red_700)
                    PullRequestState.MERGED.name == item.state -> ContextCompat.getColor(context, R.color.material_indigo_700)
                    PullRequestState.`$UNKNOWN`.name == item.state -> ContextCompat.getColor(context, R.color.material_green_700)
                    else -> ContextCompat.getColor(context, R.color.material_green_700)
                })
            }
        }
    }
}