package com.fastaccess.github.base.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.fastaccess.github.base.R
import kotlinx.android.synthetic.main.simple_text_row_item.view.*

class SimpleListAdapter<T>(
    private val onClick: (T) -> Unit
) : ListAdapter<T, SimpleViewHolder<T>>(object : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem.hashCode() == newItem.hashCode()

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder<T> = SimpleViewHolder<T>(parent).apply {
        itemView.setOnClickListener {
            getItem(adapterPosition)?.let(onClick)
        }
    }

    override fun onBindViewHolder(holder: SimpleViewHolder<T>, position: Int) = holder.bind(getItem(position))
}

class SimpleViewHolder<T>(parent: ViewGroup) : BaseViewHolder<T>(
    LayoutInflater.from(parent.context).inflate(R.layout.simple_text_row_item, parent, false)
) {
    override fun bind(item: T) {
        itemView.textView.text = item.toString()
    }

}