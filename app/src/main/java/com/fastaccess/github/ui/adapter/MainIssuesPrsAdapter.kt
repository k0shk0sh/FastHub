package com.fastaccess.github.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.fastaccess.data.persistence.models.MainIssuesPullsModel
import com.fastaccess.github.R
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import github.type.PullRequestState
import kotlinx.android.synthetic.main.issues_prs_main_screen_row_item.view.*

class MainIssuesPrsAdapter :
        ListAdapter<MainIssuesPullsModel?, MainIssuesPrsAdapter.ViewHolder>(object : DiffUtil.ItemCallback<MainIssuesPullsModel?>() {
            override fun areItemsTheSame(oldItem: MainIssuesPullsModel, newItem: MainIssuesPullsModel) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: MainIssuesPullsModel, newItem: MainIssuesPullsModel) = oldItem == newItem
        }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class ViewHolder(parent: ViewGroup) : BaseViewHolder<MainIssuesPullsModel>(LayoutInflater.from(parent.context)
            .inflate(R.layout.issues_prs_main_screen_row_item, parent, false)) {

        @SuppressLint("SetTextI18n")
        override fun bind(item: MainIssuesPullsModel) {
            itemView.let {
                it.issueTitle.text = "${item.title}#${item.number}"
                it.issueDescription.apply {
                    text = item.repoName ?: ""
                    isVisible = !item.repoName.isNullOrBlank()
                }
                it.commentCount.isVisible = item.commentCounts != 0L
                it.commentCount.text = "${item.commentCounts ?: 0}"
                if (!item.state.isNullOrEmpty()) { // PR
                    val context = it.context
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
}