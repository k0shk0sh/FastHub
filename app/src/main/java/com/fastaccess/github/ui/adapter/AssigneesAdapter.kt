package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.fastaccess.data.model.ShortUserModel
import com.fastaccess.github.ui.adapter.viewholder.AssigneeViewHolder

/**
 * Created by Kosh on 20.01.19.
 */
class AssigneesAdapter(
    val selection: HashSet<ShortUserModel>,
    val deselection: HashSet<ShortUserModel>
) : ListAdapter<ShortUserModel, AssigneeViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssigneeViewHolder = AssigneeViewHolder(parent).apply {
        itemView.let { view ->
            view.setOnClickListener {
                val position = adapterPosition
                val item = getItem(position)
                if (!selection.contains(item)) {
                    selection.add(item)
                    deselection.remove(item)
                } else {
                    deselection.add(item)
                    selection.remove(item)
                }
                notifyItemChanged(position)
            }
        }
    }

    override fun onBindViewHolder(holder: AssigneeViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, selection.contains(item))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ShortUserModel?>() {
            override fun areItemsTheSame(oldItem: ShortUserModel, newItem: ShortUserModel): Boolean = oldItem.name == newItem.name
            override fun areContentsTheSame(oldItem: ShortUserModel, newItem: ShortUserModel): Boolean = oldItem == newItem
        }
    }
}
