package com.fastaccess.github.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.model.parcelable.LabelModel
import com.fastaccess.github.R
import com.fastaccess.github.base.adapter.BaseViewHolder
import kotlinx.android.synthetic.main.label_row_item.view.*

/**
 * Created by Kosh on 04.11.18.
 */
class LabelViewHolder(parent: ViewGroup) : BaseViewHolder<LabelModel>(LayoutInflater.from(parent.context)
    .inflate(R.layout.label_row_item, parent, false)) {
    override fun bind(item: LabelModel) = Unit

    fun bind(item: LabelModel, isSelected: Boolean) {
        itemView.apply {
            backgroundLayout.isVisible = isSelected
            label.text = item.name
        }
    }
}