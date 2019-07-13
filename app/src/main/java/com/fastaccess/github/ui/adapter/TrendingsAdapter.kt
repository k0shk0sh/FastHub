package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.fastaccess.data.model.TrendingModel
import com.fastaccess.github.ui.adapter.viewholder.TrendingViewHolder

/**
 * Created by Kosh on 20.01.19.
 */
class TrendingsAdapter : ListAdapter<TrendingModel, TrendingViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder = TrendingViewHolder(parent)
    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrendingModel?>() {
            override fun areItemsTheSame(oldItem: TrendingModel, newItem: TrendingModel): Boolean = oldItem.title == newItem.title
            override fun areContentsTheSame(oldItem: TrendingModel, newItem: TrendingModel): Boolean = oldItem == newItem
        }
    }
}
