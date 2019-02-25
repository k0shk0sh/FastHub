package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.fastaccess.data.model.parcelable.LabelModel
import com.fastaccess.github.ui.adapter.viewholder.LabelViewHolder

/**
 * Created by Kosh on 20.01.19.
 */
class LabelsAdapter : ListAdapter<LabelModel, LabelViewHolder>(DIFF_CALLBACK) {

    val selection = hashSetOf<LabelModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder = LabelViewHolder(parent).apply {
        itemView.let { view ->
            view.setOnClickListener {
                val position = adapterPosition
                val item = getItem(position)
                if (!selection.contains(item)) {
                    selection.add(item)
                } else {
                    selection.remove(item)
                }
                notifyItemChanged(position)
            }
        }
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, selection.contains(item))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LabelModel?>() {
            override fun areItemsTheSame(oldItem: LabelModel, newItem: LabelModel): Boolean = oldItem.name == newItem.name
            override fun areContentsTheSame(oldItem: LabelModel, newItem: LabelModel): Boolean = oldItem == newItem
        }
    }
}
