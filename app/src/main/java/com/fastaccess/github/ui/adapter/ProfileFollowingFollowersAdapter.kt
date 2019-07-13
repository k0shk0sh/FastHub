package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.data.persistence.models.FollowingFollowerModel
import com.fastaccess.github.ui.adapter.base.BasePagedAdapter
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import com.fastaccess.github.ui.adapter.viewholder.ProfileFollowerFollowingViewHolder

/**
 * Created by Kosh on 12.10.18.
 */
class ProfileFollowingFollowersAdapter(private val onClick: (url: String) -> Unit) : BasePagedAdapter<FollowingFollowerModel>(DIFF_CALLBACK) {

    override fun contentViewHolder(parent: ViewGroup): BaseViewHolder<FollowingFollowerModel> = ProfileFollowerFollowingViewHolder(parent).apply {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position == RecyclerView.NO_POSITION) return@setOnClickListener
            getItem(position)?.url?.let(onClick)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FollowingFollowerModel?>() {
            override fun areItemsTheSame(oldItem: FollowingFollowerModel, newItem: FollowingFollowerModel): Boolean = oldItem.login == newItem.login
            override fun areContentsTheSame(oldItem: FollowingFollowerModel, newItem: FollowingFollowerModel): Boolean = oldItem == newItem
        }
    }
}