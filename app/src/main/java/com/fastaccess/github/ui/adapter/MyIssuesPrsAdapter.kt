package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.data.persistence.models.MyIssuesPullsModel
import com.fastaccess.github.ui.adapter.viewholder.MyIssuesPrsViewHolder

/**
 * Created by Kosh on 20.01.19.
 */
class MyIssuesPrsAdapter(
    private val onClick: (model: MyIssuesPullsModel) -> Unit
) : ListAdapter<MyIssuesPullsModel, MyIssuesPrsViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyIssuesPrsViewHolder = MyIssuesPrsViewHolder(parent).apply {
        itemView.setOnClickListener { _ ->
            val position = adapterPosition
            if (position == RecyclerView.NO_POSITION) return@setOnClickListener
            kotlin.runCatching { getItem(position) }.onSuccess {
                onClick(it)
            }
        }
    }

    override fun onBindViewHolder(holder: MyIssuesPrsViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MyIssuesPullsModel?>() {
            override fun areItemsTheSame(oldItem: MyIssuesPullsModel, newItem: MyIssuesPullsModel): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: MyIssuesPullsModel, newItem: MyIssuesPullsModel): Boolean = oldItem == newItem
        }
    }
}
