package com.fastaccess.ui.adapter

import android.view.ViewGroup
import com.fastaccess.data.dao.timeline.PullRequestReviewModel
import com.fastaccess.ui.adapter.callback.OnToggleView
import com.fastaccess.ui.adapter.viewholder.ReviewCommentsViewHolder
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by kosh on 15/08/2017.
 */
class ReviewCommentsAdapter(data: List<PullRequestReviewModel>,
                            listener: BaseViewHolder.OnItemClickListener<PullRequestReviewModel>,
                            var onToggleView: OnToggleView)
    : BaseRecyclerAdapter<PullRequestReviewModel, ReviewCommentsViewHolder,
        BaseViewHolder.OnItemClickListener<PullRequestReviewModel>>(data, listener) {

    override fun viewHolder(parent: ViewGroup, viewType: Int): ReviewCommentsViewHolder {
        return ReviewCommentsViewHolder.newInstance(parent, this, onToggleView)
    }

    override fun onBindView(holder: ReviewCommentsViewHolder, position: Int) {
        holder.bind(data[position])
    }
}