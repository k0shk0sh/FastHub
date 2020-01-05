package com.fastaccess.fasthub.reviews.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fastaccess.fasthub.reviews.R
import com.fastaccess.github.base.adapter.BaseViewHolder

class AddCommentViewHolder(parent: ViewGroup) : BaseViewHolder<String>(
    LayoutInflater.from(parent.context).inflate(R.layout.add_commnet_row_item, parent, false)
) {
    override fun bind(item: String) {}
}