package com.fastaccess.github.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.fastaccess.data.persistence.models.FeedModel
import com.fastaccess.github.R
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.feeds_main_screen_row_item.view.*

/**
 * Created by Kosh on 26.06.18.
 */
class FeedsAdapter : ListAdapter<FeedModel, FeedsAdapter.ViewHolder>(object : DiffUtil.ItemCallback<FeedModel?>() {
    override fun areItemsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean = oldItem.id == newItem.id
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class ViewHolder(parent: ViewGroup) : BaseViewHolder<FeedModel>(LayoutInflater.from(parent.context)
            .inflate(R.layout.feeds_main_screen_row_item, parent, false)) {

        override fun bind(item: FeedModel) {
            itemView.apply {
                feedTitle.text = context.getString(item.type?.titleId ?: 0)
            }
        }
    }
}