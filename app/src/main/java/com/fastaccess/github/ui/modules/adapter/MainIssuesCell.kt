package com.fastaccess.github.ui.modules.adapter

import android.content.Context
import androidx.core.view.isVisible
import com.fastaccess.data.persistence.models.MainIssuesPullsModel
import com.fastaccess.github.R
import com.jaychang.srv.kae.SimpleCell
import kotlinx.android.synthetic.main.issues_prs_main_screen_row_item.view.*

/**
 * Created by Kosh on 17.06.18.
 */
class MainIssuesCell(private val node: MainIssuesPullsModel) : SimpleCell<MainIssuesPullsModel>(node) {

    override fun getLayoutRes(): Int = R.layout.issues_prs_main_screen_row_item

    override fun onBindViewHolder(holder: com.jaychang.srv.kae.SimpleViewHolder, position: Int, context: Context, payload: Any?) {
        holder.itemView.let {
            it.issueTitle.text = "${node.title}#${node.number}"
            it.issueDescription.text = node.repoName
            it.commentCount.isVisible = node.commentCounts != 0L
            it.commentCount.text = "${node.commentCounts ?: 0}"
        }
    }

}