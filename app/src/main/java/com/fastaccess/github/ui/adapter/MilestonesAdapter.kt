package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.fastaccess.data.model.parcelable.MilestoneModel
import com.fastaccess.github.ui.adapter.viewholder.MilestoneViewHolder

/**
 * Created by Kosh on 20.01.19.
 */
class MilestonesAdapter(
    private val clickCallback: (MilestoneModel) -> Unit
) : ListAdapter<MilestoneModel, MilestoneViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MilestoneViewHolder = MilestoneViewHolder(parent).apply {
        itemView.let { view ->
            view.setOnClickListener {
                getItem(adapterPosition)?.let(clickCallback)
            }
        }
    }

    override fun onBindViewHolder(holder: MilestoneViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MilestoneModel?>() {
            override fun areItemsTheSame(oldItem: MilestoneModel, newItem: MilestoneModel): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: MilestoneModel, newItem: MilestoneModel): Boolean = oldItem == newItem
        }
    }
}
