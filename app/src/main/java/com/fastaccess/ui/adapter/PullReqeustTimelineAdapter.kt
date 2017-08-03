package com.fastaccess.ui.adapter

import android.view.ViewGroup
import com.fastaccess.data.dao.timeline.PullRequestTimelineModel
import com.fastaccess.ui.adapter.viewholder.PullStatusViewHolder
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by kosh on 03/08/2017.
 */
class PullReqeustTimelineAdapter constructor(val data: ArrayList<PullRequestTimelineModel>) : BaseRecyclerAdapter<PullRequestTimelineModel,
        BaseViewHolder<PullRequestTimelineModel>, BaseViewHolder.OnItemClickListener<PullRequestTimelineModel>>(data) {

    override fun viewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<PullRequestTimelineModel>? {
        when (viewType) {
            PullRequestTimelineModel.STATUS -> PullStatusViewHolder.newInstance(parent)
        }
        return null
    }

    override fun onBindView(holder: BaseViewHolder<PullRequestTimelineModel>?, position: Int) {
        val item = data[position]
        if (item.type == PullRequestTimelineModel.STATUS) {
            (holder as PullStatusViewHolder).bind(item.status)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val timeline: PullRequestTimelineModel? = data[position]
        return timeline?.type ?: super.getItemViewType(position)
    }
}