package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.data.model.ShortUserModel
import com.fastaccess.github.ui.adapter.viewholder.ShortUserViewHolder

/**
 * Created by Kosh on 12.10.18.
 */
class ShortUsersAdapter(private val onClick: (url: String) -> Unit) : ListAdapter<ShortUserModel, ShortUserViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortUserViewHolder = ShortUserViewHolder(parent).apply {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position == RecyclerView.NO_POSITION) return@setOnClickListener
            getItem(position)?.url?.let(onClick)
        }
    }

    override fun onBindViewHolder(holder: ShortUserViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ShortUserModel?>() {
            override fun areItemsTheSame(oldItem: ShortUserModel, newItem: ShortUserModel): Boolean = oldItem.login == newItem.login
            override fun areContentsTheSame(oldItem: ShortUserModel, newItem: ShortUserModel): Boolean = oldItem == newItem
        }
    }
}