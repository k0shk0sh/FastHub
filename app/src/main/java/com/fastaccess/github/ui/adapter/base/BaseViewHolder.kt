package com.fastaccess.github.ui.adapter.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Kosh on 23.06.18.
 */
abstract class BaseViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: T)
    open fun onDetached() = Unit
}