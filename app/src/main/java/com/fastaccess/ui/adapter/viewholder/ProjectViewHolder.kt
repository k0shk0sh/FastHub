package com.fastaccess.ui.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import com.fastaccess.R
import com.fastaccess.helper.ParseDateFormat
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import github.RepoProjectsOpenQuery

/**
 * Created by kosh on 09/09/2017.
 */
class ProjectViewHolder(view: View, adapter: BaseRecyclerAdapter<*, *, *>) : BaseViewHolder<RepoProjectsOpenQuery.Node>(view, adapter) {

    @BindView(R.id.description) lateinit var description: FontTextView
    @BindView(R.id.title) lateinit var title: FontTextView
    @BindView(R.id.date) lateinit var date: FontTextView

    override fun bind(t: RepoProjectsOpenQuery.Node) {
        title.text = t.name()
        if (t.body().isNullOrBlank()) {
            description.visibility = View.GONE
        } else {
            description.visibility = View.VISIBLE
            description.text = t.body()
        }
        date.text = ParseDateFormat.getTimeAgo(t.createdAt().toString())
    }

    companion object {
        fun newInstance(parent: ViewGroup, adapter: BaseRecyclerAdapter<*, *, *>): ProjectViewHolder {
            return ProjectViewHolder(getView(parent, R.layout.feeds_row_no_image_item), adapter)
        }
    }
}