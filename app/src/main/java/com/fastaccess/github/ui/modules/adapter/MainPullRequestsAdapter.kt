package com.fastaccess.github.ui.modules.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.fastaccess.github.R
import com.jaychang.srv.kae.SimpleCell
import github.GetPullRequestsQuery
import github.type.PullRequestState
import kotlinx.android.synthetic.main.issues_prs_main_screen_row_item.view.*

/**
 * Created by Kosh on 17.06.18.
 */
class MainPullRequestsAdapter(private val node: GetPullRequestsQuery.Node) : SimpleCell<GetPullRequestsQuery.Node>(node) {

    override fun getLayoutRes(): Int = R.layout.issues_prs_main_screen_row_item

    override fun onBindViewHolder(holder: com.jaychang.srv.kae.SimpleViewHolder, position: Int, context: Context, payload: Any?) {
        holder.itemView.let {
            it.issueTitle.text = "${node.title}#${node.number}"
            it.issueDescription.text = node.repository.nameWithOwner
            it.commentCount.isVisible = node.comments.totalCount > 0
            it.commentCount.text = "${node.comments.totalCount}"
            it.state.isVisible = true
            it.state.text = node.state.name
            it.state.setTextColor(when (node.state) {
                PullRequestState.OPEN -> ContextCompat.getColor(context, R.color.material_green_700)
                PullRequestState.CLOSED -> ContextCompat.getColor(context, R.color.material_red_700)
                PullRequestState.MERGED -> ContextCompat.getColor(context, R.color.material_indigo_700)
                PullRequestState.`$UNKNOWN` -> ContextCompat.getColor(context, R.color.material_green_700)
            })
        }
    }

}