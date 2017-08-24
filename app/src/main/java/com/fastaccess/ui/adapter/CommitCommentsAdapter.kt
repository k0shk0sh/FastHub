package com.fastaccess.ui.adapter

import android.view.ViewGroup
import com.fastaccess.data.dao.timeline.PullRequestCommitModel
import com.fastaccess.ui.adapter.callback.OnToggleView
import com.fastaccess.ui.adapter.viewholder.CommitCommentsViewHolder
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

/**
 * Created by kosh on 15/08/2017.
 */
class CommitCommentsAdapter(data: List<PullRequestCommitModel>,
                            listener: BaseViewHolder.OnItemClickListener<PullRequestCommitModel>,
                            var onToggleView: OnToggleView)
    : BaseRecyclerAdapter<PullRequestCommitModel, CommitCommentsViewHolder,
        BaseViewHolder.OnItemClickListener<PullRequestCommitModel>>(data, listener) {

    override fun viewHolder(parent: ViewGroup, viewType: Int): CommitCommentsViewHolder {
        return CommitCommentsViewHolder.newInstance(parent, this, onToggleView)
    }

    override fun onBindView(holder: CommitCommentsViewHolder, position: Int) {
        holder.bind(data[position])
    }
}