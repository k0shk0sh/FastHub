package com.fastaccess.ui.adapter

import android.view.ViewGroup
import com.fastaccess.data.dao.timeline.PullRequestTimelineModel
import com.fastaccess.ui.adapter.callback.OnToggleView
import com.fastaccess.ui.adapter.callback.ReactionsCallback
import com.fastaccess.ui.adapter.viewholder.*
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder.OnItemClickListener

/**
 * Created by kosh on 03/08/2017.
 */
class PullRequestTimelineAdapter constructor(private val data: ArrayList<PullRequestTimelineModel>,
                                             private var onToggleView: OnToggleView,
                                             private var reactionsCallback: ReactionsCallback,
                                             internal var isMerged: Boolean = false,
                                             private var repoOwner: String,
                                             private var poster: String) : BaseRecyclerAdapter<PullRequestTimelineModel,
        BaseViewHolder<PullRequestTimelineModel>, OnItemClickListener<PullRequestTimelineModel>>(data) {

    override fun viewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<PullRequestTimelineModel> {
        when (viewType) {
            PullRequestTimelineModel.HEADER -> return PullRequestDetailsViewHolder.newInstance(parent, this,
                    onToggleView, reactionsCallback, repoOwner, poster)
            PullRequestTimelineModel.STATUS -> return PullStatusViewHolder.newInstance(parent)
            PullRequestTimelineModel.COMMENT -> return PullRequestTimelineCommentsViewHolder.newInstance(parent, this, onToggleView)
            PullRequestTimelineModel.REVIEW -> return ReviewsViewHolder.newInstance(parent, this, onToggleView)
            PullRequestTimelineModel.COMMIT_COMMENTS -> return CommitThreadViewHolder.newInstance(parent, this, onToggleView)
            else -> return PullRequestEventViewHolder.newInstance(parent, this)
        }
    }

    override fun onBindView(holder: BaseViewHolder<PullRequestTimelineModel>, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int {
        val timeline: PullRequestTimelineModel? = data[position]
        return timeline?.type ?: super.getItemViewType(position)
    }
}