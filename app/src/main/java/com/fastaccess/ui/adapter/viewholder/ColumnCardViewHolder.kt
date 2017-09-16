package com.fastaccess.ui.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.data.dao.ProjectCardModel
import com.fastaccess.data.dao.PullsIssuesParser
import com.fastaccess.helper.ParseDateFormat
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by Hashemsergani on 11.09.17.
 */
class ColumnCardViewHolder private constructor(item: View, adapter: BaseRecyclerAdapter<*, *, *>, val isOwner: Boolean)
    : BaseViewHolder<ProjectCardModel>(item, adapter) {

    @BindView(R.id.title) lateinit var title: TextView
    @BindView(R.id.addedBy) lateinit var addedBy: TextView
    @BindView(R.id.editCard) lateinit var editCard: View

    init {
        editCard.setOnClickListener(this)
    }

    override fun bind(t: ProjectCardModel) {
        title.text = if (t.note.isNullOrBlank()) {
            val issue = PullsIssuesParser.getForIssue(t.contentUrl)
            if (issue != null) {
                "${issue.login}/${issue.repoId}/${issue.number}"
            } else {
                val pr = PullsIssuesParser.getForPullRequest(t.contentUrl)
                if (pr != null) {
                    "${pr.login}/${pr.repoId}/${pr.number}"
                } else {
                    "(FastHub) - to be fixed by GitHub! Sorry!"
                }
            }
        } else {
            t.note
        }
        addedBy.text = itemView.context.getString(R.string.card_added_by, t.creator?.login, ParseDateFormat.getTimeAgo(t.createdAt))
    }

    companion object {
        fun newInstance(parent: ViewGroup, adapter: BaseRecyclerAdapter<*, *, *>, isOwner: Boolean): ColumnCardViewHolder {
            return ColumnCardViewHolder(getView(parent, R.layout.column_card_row_layout), adapter, isOwner)
        }
    }
}