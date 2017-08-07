package com.fastaccess.ui.adapter

import android.view.ViewGroup
import com.fastaccess.data.dao.timeline.PullRequestTimelineModel
import com.fastaccess.ui.adapter.callback.OnToggleView
import com.fastaccess.ui.adapter.callback.ReactionsCallback
import com.fastaccess.ui.adapter.viewholder.PullRequestDetailsViewHolder
import com.fastaccess.ui.adapter.viewholder.PullRequestEventViewHolder
import com.fastaccess.ui.adapter.viewholder.PullStatusViewHolder
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by kosh on 03/08/2017.
 */
class PullRequestTimelineAdapter constructor(val data: ArrayList<PullRequestTimelineModel>,
                                             internal var onToggleView: OnToggleView,
                                             internal var reactionsCallback: ReactionsCallback,
                                             internal var isMerged: Boolean = false,
                                             internal var repoOwner: String,
                                             internal var poster: String)
    : BaseRecyclerAdapter<PullRequestTimelineModel,
        BaseViewHolder<PullRequestTimelineModel>, BaseViewHolder.OnItemClickListener<PullRequestTimelineModel>>(data) {

    override fun viewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<PullRequestTimelineModel> {
        when (viewType) {
            PullRequestTimelineModel.HEADER -> return PullRequestDetailsViewHolder.newInstance(parent, this,
                    onToggleView, reactionsCallback, repoOwner, poster)
            PullRequestTimelineModel.STATUS -> return PullStatusViewHolder.newInstance(parent)
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