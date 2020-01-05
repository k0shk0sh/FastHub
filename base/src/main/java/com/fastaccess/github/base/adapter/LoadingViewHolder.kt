package com.fastaccess.github.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fastaccess.github.base.R

/**
 * Created by Kosh on 12.10.18.
 */
class LoadingViewHolder<A>(parent: ViewGroup) : BaseViewHolder<A>(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.progress_layout, parent, false)
) {
    override fun bind(item: A) = Unit
}