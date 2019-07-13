package com.fastaccess.github.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fastaccess.data.model.parcelable.MilestoneModel
import com.fastaccess.github.R
import com.fastaccess.github.extensions.DatePrettifier
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.milestone_row_item.view.*

/**
 * Created by Kosh on 04.11.18.
 */
class MilestoneViewHolder(parent: ViewGroup) : BaseViewHolder<MilestoneModel>(LayoutInflater.from(parent.context)
    .inflate(R.layout.milestone_row_item, parent, false)) {

    override fun bind(item: MilestoneModel) {
        itemView.apply {
            title.text = item.title ?: "${item.number ?: "N/A"}"
            dueOn.text = item.dueOn?.let {
                DatePrettifier.prettifyDate(it.time)
            }
        }
    }
}