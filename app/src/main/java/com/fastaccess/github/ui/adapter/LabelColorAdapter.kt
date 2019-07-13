package com.fastaccess.github.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fastaccess.github.ui.adapter.viewholder.LabelColorViewHolder

/**
 * Created by Kosh on 07.03.19.
 */
class LabelColorAdapter(
    private val list: List<String>,
    private val callback: (String) -> Unit
) : RecyclerView.Adapter<LabelColorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelColorViewHolder = LabelColorViewHolder(parent).apply {
        itemView.setOnClickListener {
            callback.invoke(list[adapterPosition])
        }
    }

    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: LabelColorViewHolder, position: Int) = holder.bind(list[position])
}