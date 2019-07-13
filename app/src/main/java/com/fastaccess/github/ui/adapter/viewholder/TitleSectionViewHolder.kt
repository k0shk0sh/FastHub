package com.fastaccess.github.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fastaccess.github.R
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.title_section_row_item.view.*

/**
 * Created by Kosh on 04.11.18.
 */
class TitleSectionViewHolder(parent: ViewGroup) : BaseViewHolder<CharSequence>(
    LayoutInflater.from(parent.context).inflate(R.layout.title_section_row_item, parent, false)
) {
    override fun bind(item: CharSequence) {
        itemView.title.text = item
    }
}