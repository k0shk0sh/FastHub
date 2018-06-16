package com.fastaccess.github.ui.modules.adapter

import android.content.Context
import androidx.core.view.isVisible
import com.fastaccess.github.R
import com.jaychang.srv.kae.SimpleCell
import github.GetIssuesQuery
import kotlinx.android.synthetic.main.issues_prs_main_screen_row_item.view.*

/**
 * Created by Kosh on 17.06.18.
 */
class MainIssuesAdapter(private val node: GetIssuesQuery.Node) : SimpleCell<GetIssuesQuery.Node>(node) {

    override fun getLayoutRes(): Int = R.layout.issues_prs_main_screen_row_item

    override fun onBindViewHolder(holder: com.jaychang.srv.kae.SimpleViewHolder, position: Int, context: Context, payload: Any?) {
        holder.itemView.let {
            it.issueTitle.text = "${node.title}#${node.number}"
            it.issueDescription.text = node.repository.nameWithOwner
            it.commentCount.isVisible = node.comments.totalCount > 0
            it.commentCount.text = "${node.comments.totalCount}"
        }
    }

}