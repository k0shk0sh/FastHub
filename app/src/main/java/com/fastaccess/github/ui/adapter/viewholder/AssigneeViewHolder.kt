package com.fastaccess.github.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fastaccess.data.model.ShortUserModel
import com.fastaccess.github.R
import com.fastaccess.github.ui.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.assignees_row_item.view.*

/**
 * Created by Kosh on 04.11.18.
 */
class AssigneeViewHolder(parent: ViewGroup) : BaseViewHolder<ShortUserModel>(LayoutInflater.from(parent.context)
    .inflate(R.layout.assignees_row_item, parent, false)) {
    override fun bind(item: ShortUserModel) = Unit

    fun bind(item: ShortUserModel, isSelected: Boolean) {
        itemView.apply {
            backgroundLayout.isVisible = isSelected
            userIcon.loadAvatar(item.avatarUrl, item.url)
            title.text = item.login ?: item.name ?: ""
        }
    }
}