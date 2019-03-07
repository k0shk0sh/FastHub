package com.fastaccess.github.ui.adapter.viewholder

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.fastaccess.github.R
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.label_color_row_item.view.*

/**
 * Created by Kosh on 12.10.18.
 */
class LabelColorViewHolder(parent: ViewGroup) : BaseViewHolder<String>(LayoutInflater.from(parent.context)
    .inflate(R.layout.label_color_row_item, parent, false)) {

    override fun bind(item: String) {
        itemView.apply {
            colorChip.text = item
            colorChip.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(item))
        }
    }
}