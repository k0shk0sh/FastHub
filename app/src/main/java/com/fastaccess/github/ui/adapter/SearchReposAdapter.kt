package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.fastaccess.data.model.ShortRepoModel
import com.fastaccess.github.ui.adapter.viewholder.ShortRepoViewHolder

/**
 * Created by Kosh on 20.01.19.
 */
class SearchReposAdapter : ListAdapter<ShortRepoModel, ShortRepoViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortRepoViewHolder = ShortRepoViewHolder(parent)
    override fun onBindViewHolder(holder: ShortRepoViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ShortRepoModel?>() {
            override fun areItemsTheSame(oldItem: ShortRepoModel, newItem: ShortRepoModel): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: ShortRepoModel, newItem: ShortRepoModel): Boolean = oldItem == newItem
        }
    }
}
