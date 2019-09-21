package com.fastaccess.fasthub.diff.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.fastaccess.data.model.CommitLinesModel
import com.fastaccess.fasthub.diff.R
import com.fastaccess.github.base.adapter.BaseViewHolder
import com.fastaccess.github.extensions.getColorAttr
import com.fastaccess.github.extensions.getDrawableCompat
import com.fastaccess.markdown.widget.SpannableBuilder
import kotlinx.android.synthetic.main.commit_line_row_item.view.*

class CommitLinesAdapter(
    private val callback: (CommitLinesModel) -> Unit
) : ListAdapter<CommitLinesModel, CommitLineViewHolder>(object : DiffUtil.ItemCallback<CommitLinesModel?>() {
    override fun areItemsTheSame(oldItem: CommitLinesModel, newItem: CommitLinesModel): Boolean = oldItem.position == newItem.position
    override fun areContentsTheSame(oldItem: CommitLinesModel, newItem: CommitLinesModel): Boolean = oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CommitLineViewHolder(parent).apply {
        itemView.setOnClickListener { getItem(adapterPosition)?.let(callback) }
    }

    override fun onBindViewHolder(holder: CommitLineViewHolder, position: Int) = holder.bind(getItem(position))

}

class CommitLineViewHolder(parent: ViewGroup) : BaseViewHolder<CommitLinesModel>(
    LayoutInflater.from(parent.context).inflate(R.layout.commit_line_row_item, parent, false)
) {
    override fun bind(item: CommitLinesModel) {
        itemView.apply {
            leftLinNo.text = if (item.leftLineNo > 0) "${item.leftLineNo}" else "  "
            rightLinNo.text = if (item.rightLineNo > 0) "${item.rightLineNo}" else "  "
            hasComment.isVisible = item.hasCommentedOn
            when (item.color) {
                CommitLinesModel.ADDITION -> textView.setBackgroundColor(context.getColorAttr(R.attr.patch_addition))
                CommitLinesModel.DELETION -> textView.setBackgroundColor(context.getColorAttr(R.attr.patch_deletion))
                CommitLinesModel.PATCH -> {
                    leftLinNo.visibility = View.GONE
                    rightLinNo.visibility = View.GONE
                    textView.setBackgroundColor(context.getColorAttr(R.attr.patch_ref))
                }
                else -> textView.setBackgroundColor(Color.TRANSPARENT)
            }
            if (item.noNewLine) {
                textView.text = SpannableBuilder.builder().append(item.text).append(" ")
                    .append(context.getDrawableCompat(R.drawable.ic_newline))
            } else {
                textView.setText(item.text)
            }
        }
    }
}