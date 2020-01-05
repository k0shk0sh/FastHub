package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.data.persistence.models.LoginModel
import com.fastaccess.github.ui.adapter.viewholder.LoggedInUserViewHolder

/**
 * Created by Kosh on 12.10.18.
 */
class LoggedInUsersAdapter(private val onClick: (user: LoginModel) -> Unit) : ListAdapter<LoginModel, LoggedInUserViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoggedInUserViewHolder = LoggedInUserViewHolder(parent).apply {
        itemView.setOnClickListener {
            val position = adapterPosition
            if (position == RecyclerView.NO_POSITION) return@setOnClickListener
            getItem(position)?.let(onClick)
        }
    }

    override fun onBindViewHolder(holder: LoggedInUserViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LoginModel?>() {
            override fun areItemsTheSame(oldItem: LoginModel, newItem: LoginModel): Boolean = oldItem.login == newItem.login
            override fun areContentsTheSame(oldItem: LoginModel, newItem: LoginModel): Boolean = oldItem == newItem
        }
    }
}